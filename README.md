<div align="center">

  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="128" height="128" alt="Echo Logo">

  <h1>Echo</h1>

  <p><b>Your Private, Local AI Companion on Android.</b></p>

  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.1.0-blue?style=for-the-badge&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/Ollama-black?style=for-the-badge&logo=ollama" alt="Ollama">
  </p>

  <br>
</div>

---

## Key Features

*   **Local-First Architecture**: Connects directly to local Ollama instances via the network. No data leaves your infrastructure.
*   **Privacy-Centric**: Designed for security with no cloud dependency, no telemetry, and no accounts required.
*   **Dynamic Configuration**: Full control over server endpoints and custom request headers.
*   **Material 3 Interface**: Modern, adaptive UI supporting system dark mode and AMOLED themes.
*   **Advanced Model Management**: Built-in selector for switching between local models (Llama, Mistral, Phi, etc.) with detailed model metadata inspection.
*   **Personalization**: Persistent user profiles and system instructions to tailor model behavior.

## Technical Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Concurrency**: Kotlin Coroutines & Flow
*   **Dependency Injection**: Koin
*   **Database**: Room (with Kotlin Serialization for complex types)
*   **Navigation**: Navigation 3
*   **Networking**: Ollama-Kotlin (Ktor-based)

## Getting Started

### Prerequisites

1.  **Ollama Instance**: Ensure Ollama is installed and running on your host machine.
2.  **Server Configuration**: Set `OLLAMA_HOST=0.0.0.0` on your host to allow connections from your Android device.
3.  **Network Access**: Your Android device must be on the same local network as the Ollama server.

### Installation

*   Download the latest build from the [Releases](https://github.com/udhay-adithya/kollama/releases) section.
*   Install on any device running Android 15 or higher.

## Configuration

Navigate to **Settings > Connection Settings** to configure your instance:
- **Host URL**: Specify your server's IP and port (e.g., `http://192.168.1.100:11434`).
- **Custom Headers**: Add any necessary authentication or custom headers required by your proxy or server.

## Contributing

Contributions are welcome. Please adhere to standard open-source workflows:
1. Fork the repository.
2. Create a feature branch.
3. Submit a Pull Request with a clear description of changes.

## License

Distributed under the MIT License. See `LICENSE` for the full text.

---
<div align="center">
  <sub>Built with ❤️ by Udhay Adithya</sub>
</div>