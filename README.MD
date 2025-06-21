# Sarvam AI Unofficial SDK (Chat Module)

An unofficial Kotlin-based Android SDK for interacting with the Sarvam AI chat API. Currently, **only chat** is supported; support for translation, speech-to-text (STT), text-to-speech (TTS), and other modalities will be added in future releases.

---

## 📦 Features

* **Chat Completion**: Send a conversation history to Sarvam AI and receive streaming or non‑streaming replies.
* **Streaming Support**: Real‑time token streaming via SSE (OkHttp-SSE).
* **Simple Retrofit Integration**: Non‑streaming chat via Retrofit + Moshi.
* **Easy API Key Management**: Pull your API key from `local.properties` or environment variables.

---

## 🚀 Installation

1. **Add the Maven module** (or copy the `sarvam-sdk` directory) into your Android project.

2. In your **app module** `settings.gradle.kts`, include:

   ```kotlin
   include(":sarvam-sdk")
   ```

3. **Add the SDK dependency** in your app `build.gradle.kts`:

```kotlin
implementation(project(":sarvam-sdk"))
```

All required network, SSE, Retrofit, and Moshi dependencies are provided transitively by the SDK.

4. **Configure your API key** in `local.properties`:
````
```properties
SARVAM_API_KEY=sk_...
````

Or set the environment variable `API_KEY` before building.

---

## 💡 Usage

### 1. Initialize in your Android App

In your `Application` or `MainActivity`, no special initialization is required—just call the client methods.

### 2. Non‑Streaming Chat (Retrofit)

```kotlin
// Prepare messages
val messages = listOf(
  Message(role = "system", content = "You are a helpful assistant."),
  Message(role = "user", content = "Hello, Sarvam!")
)
val request = ChatRequest(messages = messages)

// In a coroutine scope
lifecycleScope.launch {
  try {
    val response = ApiClient.sendChat(request)
    val reply = response.choices.first().message.content
    println("Assistant: $reply")
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
```

### 3. Streaming Chat (OkHttp-SSE)

```kotlin
// Append to your UI state
ApiClient.streamChat(
  request = ChatRequest(messages = messages),
  onTokenReceived = { token -> /* append to text buffer */ },
  onComplete     = { /* finalize UI */       },
  onError        = { error -> /* show error */}
)
```

Integrate into your **ViewModel** and **Compose UI** as demonstrated in the `sample-app`.

---

## 📂 Sample App

A minimal Jetpack Compose sample is provided in the `app/` directory, showcasing:

* Chat screen with **scroll-to-bottom**, **keyboard avoidance**, and **rich text** parsing.
* ViewModel wiring for both streaming and non‑streaming calls.

---

## 🛣 Roadmap

* **v0.2**: Translation API support (text ↔ text).
* **v0.3**: Speech-to-Text (STT) & Text-to-Speech (TTS).
* **v0.4**: Image & file upload for GPT vision.
* **v1.0**: Official GitHub release, documentation site, and sample projects.

Contributions and feedback are welcome. Feel free to open issues and PRs!

---

## 📜 License

This SDK is provided **unofficially** under the MIT License. See [LICENSE](LICENSE) for details.
