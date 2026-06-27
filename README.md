![Architecture](architecture.png)

# 📱 SonicBridge Receiver

> Listen to your Android TV audio wirelessly with ultra-low latency.

**SonicBridge Receiver** is the Android companion application for **SonicBridge TV**. It connects to your Android TV over your local Wi-Fi network, receives real-time PCM audio through WebSockets, and plays it with extremely low latency using Android's `AudioTrack`.

Perfect for private TV watching with wired or Bluetooth headphones without disturbing others.

---

## ✨ Features

- 📡 Connects to SonicBridge TV over Wi-Fi
- 🎧 Ultra-low latency audio playback
- 🔊 Stereo PCM (48 kHz, 16-bit)
- ⚡ Real-time WebSocket streaming
- 🎵 Native AudioTrack playback
- 🔄 Foreground playback service
- 📱 Modern Jetpack Compose UI
- 🌙 Lightweight and battery efficient

---

## 📸 Architecture

```
Android TV
      │
Audio Playback Capture
      │
Raw PCM Audio
      │
WebSocket
═════════ Wi-Fi ═════════
      │
SonicBridge Receiver
      │
AudioTrack
      │
Headphones / Speakers
```

---

## 🚀 Features

✅ Ultra-low latency streaming

✅ Stereo 48 kHz PCM playback

✅ Connect to any Android TV running SonicBridge TV

✅ Connection status monitoring

✅ Live transfer statistics

✅ Bytes received counter

✅ Foreground playback notification

---

## 🛠 Tech Stack

- Kotlin
- Jetpack Compose
- Ktor WebSocket Client
- AudioTrack API
- Foreground Service
- Coroutines
- Material 3

---

## 📦 Requirements

- Android 8.0 (API 26+) or later
- SonicBridge TV installed on an Android TV
- Both devices connected to the same Wi-Fi network

---

## 🚀 Getting Started

### Clone the repository

```bash
git clone https://github.com/noncoderf/SonicBridge.git
```

### Open in Android Studio

Open using the latest stable version of Android Studio.

### Build & Install

Run the application on your Android phone.

### Connect

1. Launch SonicBridge TV.
2. Start audio transmission.
3. Open SonicBridge Receiver.
4. Enter your TV IP address.
5. Tap **Connect**.
6. Enjoy real-time TV audio.

---

## 📡 Communication

The receiver connects to:

```
ws://<TV-IP>:8080/audio
```

Example

```
ws://192.168.1.16:8080/audio
```

---

## 🎼 Audio Format

| Property | Value |
|----------|------|
| Sample Rate | 48,000 Hz |
| Channels | Stereo |
| Encoding | PCM 16-bit |
| Protocol | WebSocket |

---

## 📊 Current Features

| Feature | Status |
|----------|--------|
| WebSocket Client | ✅ |
| AudioTrack Playback | ✅ |
| Stereo Audio | ✅ |
| Ultra-low Latency | ✅ |
| Foreground Service | ✅ |
| Live Statistics | ✅ |
| TV IP Setup | ✅ |
| Auto Discovery | 🚧 Planned |
| QR Pairing | 🚧 Planned |

---

## 🚀 Roadmap

- 🔍 Automatic TV Discovery (mDNS)
- 📷 QR Code Pairing
- 🎚 Volume Controls
- 🎛 Audio Equalizer
- 🔒 Secure Pairing
- 🎧 Bluetooth Audio Optimization
- 📈 Connection Quality Indicator
- 🎵 Optional Opus Codec
- 📡 Multiple TV Profiles

---

## ⚠ Limitations

- Requires SonicBridge TV running on Android TV.
- Both devices must be on the same local Wi-Fi network.
- Audio quality depends on network stability.

---

## 📺 Companion TV App

The Android TV transmitter application is available here:

**SonicBridge TV**

https://github.com/noncoderf/Wifi-Sound-Transmitter-TV-App

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

Feel free to fork the project and submit a pull request.

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Nizamuddin Ahmed**

Senior Android Developer

- GitHub: https://github.com/noncoderf
- LinkedIn: https://linkedin.com/in/nizamuddin007

---

⭐ If you found this project useful, consider giving it a **Star**!