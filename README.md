# MindMirror (Jetpack Compose)

MindMirror is a simple diary app built with Jetpack Compose.

## What it does
- Add daily feelings/experience notes
- Save entries locally in SQLite via Room
- Review all saved entries later
- Generate quick AI-style reflection suggestions locally (offline heuristic)
- Optional remote companion model (OpenAI-compatible) for richer chat replies

## Stack
- Kotlin
- Jetpack Compose
- Room
- Coroutines + Flow
- MVVM (simple ViewModel)

## Project structure
- `app/src/main/java/com/mindmirror/` main app code
- `app/src/test/java/com/mindmirror/` unit tests

## Run
Open the project in Android Studio (Hedgehog or newer), let Gradle sync, then run the `app` configuration on an emulator/device.

## Test
Run unit tests from Android Studio, or use the project Gradle wrapper from terminal:

```bash
./gradlew test
```

## Optional: Remote AI (ChatGPT-style)
MindMirror can call an OpenAI-compatible API for better conversational responses.

Add these keys to `local.properties` (do not commit secrets):

```properties
LLM_REMOTE_ENABLED=true
GROQ_API_KEY=your_api_key_here
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=llama-3.1-8b-instant
```

Backward-compatible `LLM_*` keys still work, but `GROQ_*` keys are preferred.

If these are missing, the app automatically falls back to local offline guidance.

If you want real LLM-based insights later, add a backend endpoint and call it from the repository; keep API keys out of the app binary.

