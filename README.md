# HyperDebloat

Утилита для безопасного отключения системной рекламы, телеметрии и удаления предустановленного мусора (bloatware) на Android-смартфонах через ADB.

> **Language / Язык:** [Русский](#русский) | [English](#english)

---

## Русский

### Возможности
* **Автоопределение устройства:** При подключении по USB или Wi-Fi программа считывает бренд смартфона (`Xiaomi / Redmi / POCO`, `Samsung`, `Realme / OPPO / OnePlus`, `Huawei / Honor`, `AOSP / Pixel`) и автоматически отмечает пакеты, актуальные именно для этой оболочки.
* **Отключение рекламы и трекеров:** Блокировка системных рекламных демонов (`msa`, `analytics`, `joyose`, `Customization Service` и др.).
* **Удаление системного мусора:** Безопасное удаление предустановленных приложений для текущего пользователя (`pm uninstall --user 0`) без разблокировки загрузчика и root-прав.
* **Защита «Анти-кирпич»:** Встроенный чёрный список критических системных пакетов (лаунчеры, SystemUI, настройки, службы безопасности), которые заблокированы от удаления на уровне кода.
* **Резервное копирование и откат:** Все удалённые пакеты записываются в `backups/removed_packages.json` и восстанавливаются в один клик (`cmd package install-existing`).
* **Очистка кэша:** Массовый сброс кэша приложений (`pm trim-caches`) с замером освобождённой памяти.
* **Двуязычный интерфейс:** Переключение между `RU` и `EN` без перезапуска окна.

### Как пользоваться (готовый `.exe`)
1. Скачайте актуальный **`HyperDebloat.exe`** из раздела [Releases](../../releases).
2. На телефоне включите **Параметры разработчика** → **Отладка по USB** (на устройствах Samsung предварительно отключите *Безопасность → Автоблокировка*).
3. Подключите смартфон к ПК по кабелю (или введите `IP:PORT` для подключения по Wi-Fi) и подтвердите запрос отладки на экране телефона.
4. Запустите `HyperDebloat.exe` и нажмите **Обновить**.

### Запуск из исходного кода и сборка `.exe`
Требуется **Python 3.10+** и файлы Google Platform Tools (`adb.exe`, `AdbWinApi.dll`, `AdbWinUsbApi.dll`) в папке `tools/`.

```bash
pip install -r requirements.txt
python app.py
```

Сборка в автономный `.exe`:
```bash
pyinstaller --noconfirm --onefile --windowed --name "HyperDebloat" --icon="icon.ico" --add-data "templates;templates" --add-data "static;static" --add-data "tools;tools" app.py
```
<img width="1908" height="1017" alt="image" src="https://github.com/user-attachments/assets/203e8582-3fa1-4e4e-980e-12cdc46b0390" />
<img width="1919" height="1016" alt="image" src="https://github.com/user-attachments/assets/07eec599-7957-4f34-b6f7-cc70081943fc" />


---

## English

### Features
* **Smart Device Detection:** Automatically detects the connected Android brand (`Xiaomi / Redmi / POCO`, `Samsung`, `Realme / OPPO / OnePlus`, `Huawei / Honor`, `AOSP / Pixel`) and pre-selects relevant bloatware packages.
* **Ads & Telemetry Removal:** Disables system ad daemons and background trackers without root or unlocked bootloader.
* **Anti-Brick Protection:** Hardcoded blocklist of critical system components (`SystemUI`, launchers, settings, security services) to prevent bootloops.
* **One-Click Restore:** Logs every removed package to `backups/removed_packages.json` for instant restoration.
* **Cache Trimming:** Clears app caches via `pm trim-caches` with free space reporting.
* **Bilingual UI:** Instant `RU / EN` language toggle.

### Build from Source
Place `adb.exe`, `AdbWinApi.dll`, and `AdbWinUsbApi.dll` inside the `tools/` directory, then run:

```bash
pip install -r requirements.txt
python app.py
```

