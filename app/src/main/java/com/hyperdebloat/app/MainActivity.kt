package com.hyperdebloat.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(AdbBridge(), "AndroidADB")
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class AdbBridge {
        private fun getAdbExe(): String {
            val libDir = applicationInfo.nativeLibraryDir
            val bundled = File(libDir, "libadb.so")
            return if (bundled.exists()) bundled.absolutePath else "adb"
        }

        private fun execAdb(args: List<String>): Pair<Boolean, String> {
            return try {
                val cmd = mutableListOf(getAdbExe())
                cmd.addAll(args)
                val pb = ProcessBuilder(cmd)
                pb.environment()["HOME"] = filesDir.absolutePath
                pb.environment()["TMPDIR"] = cacheDir.absolutePath
                pb.redirectErrorStream(true)
                val proc = pb.start()
                val out = proc.inputStream.bufferedReader().readText().trim()
                proc.waitFor(15, TimeUnit.SECONDS)
                Pair(proc.exitValue() == 0, out)
            } catch (e: Exception) {
                Pair(false, "Ошибка ADB: ${e.message}")
            }
        }

        @JavascriptInterface
        fun pair(target: String, code: String): String {
            val (ok, out) = execAdb(listOf("pair", target.trim(), code.trim()))
            return JSONObject().apply {
                put("success", ok && out.lowercase().contains("paired"))
                put("message", out.ifEmpty { "Нет ответа от ADB" })
            }.toString()
        }

        @JavascriptInterface
        fun connect(target: String): String {
            val (ok, out) = execAdb(listOf("connect", target.trim()))
            return JSONObject().apply {
                put("success", ok && out.lowercase().contains("connected"))
                put("message", out.ifEmpty { "Ошибка подключения" })
            }.toString()
        }

        @JavascriptInterface
        fun checkStatus(): String {
            val (ok, out) = execAdb(listOf("devices"))
            val res = JSONObject()
            if (!ok) {
                res.put("connected", false)
                res.put("message", out)
                return res.toString()
            }
            val lines = out.lines().drop(1).map { it.trim() }.filter { it.isNotEmpty() }
            val dev = lines.firstOrNull { it.contains("\tdevice") }?.substringBefore("\t")
            if (dev != null) {
                val brand = execAdb(listOf("-s", dev, "shell", "getprop", "ro.product.brand")).second
                val manuf = execAdb(listOf("-s", dev, "shell", "getprop", "ro.product.manufacturer")).second
                val model = execAdb(listOf("-s", dev, "shell", "getprop", "ro.product.model")).second
                val raw = "$brand $manuf".lowercase()
                val vendor = when {
                    raw.contains("xiaomi") || raw.contains("redmi") || raw.contains("poco") -> "xiaomi"
                    raw.contains("samsung") -> "samsung"
                    raw.contains("realme") || raw.contains("oppo") || raw.contains("oneplus") -> "bbk"
                    raw.contains("huawei") || raw.contains("honor") -> "huawei"
                    else -> "aosp"
                }
                res.put("connected", true)
                res.put("device", "${brand.uppercase()} $model")
                res.put("vendor", vendor)
                res.put("message", "П