# CodeQuest

A polished, offline-first **Android** app for data-structures & algorithms interview
preparation: an adaptive practice engine, a 1,220-question authored bank, daily sets,
lessons, coding challenges and a gamified progression layer — built entirely with
**Kotlin** and **Jetpack Compose**.

| | |
|---|---|
| **Platform** | Android (minSdk 24, target/compileSdk 37) |
| **UI** | Jetpack Compose · Material 3 · custom glass design system |
| **Architecture** | Single-activity · MVVM · repository pattern · unidirectional StateFlow |
| **Data** | Local & in-memory (seeded question bank) — no backend required |
| **Auth** | Optional Firebase (Email/Password + Google) with an offline demo mode |
| **Tests** | 34 JUnit tests across 4 suites |

---

## Overview

CodeQuest turns DSA revision into a guided loop:

1. **Learn** — a 12-topic roadmap of short lessons with inline examples.
2. **Practice** — the full question bank with topic/difficulty/status filters.
3. **Daily Practice** — a deterministic set of 10 questions pinned for the day.
4. **Level up** — a rolling adaptive engine judges every first attempt and moves
   your subject level when the evidence supports it.
5. **Progress** — XP, streaks, achievements and coding challenges track the journey.

Everything works **without a network connection or an account**: launch straight into
the demo learner, or sign in with Firebase when configured.

## Features

- **Onboarding** — pick subjects, seed your starting levels, personalise the path.
- **Adaptive leveling** — first attempts feed a rolling window; level verdicts only
  land once the window is full, so a single question can never swing a level.
- **1,220 authored questions** across 12 topics (366 Easy / 488 Medium / 366 Hard)
  in nine formats: Concept MCQ, Output Prediction, Complexity, Debugging,
  Problem Solving, Code Reading, Code Completion, Implementation, Optimisation.
- **Theme → level authoring** — each topic theme expands into ten rungs
  (define → recognise → apply → trace → analyse → read → complete → debug →
  edge case → optimise) with distractors drawn from neighbouring rungs.
- **Daily set** — 10 level-appropriate questions chosen once per day key and pinned,
  so the set never reshuffles mid-day.
- **Lessons** — bite-sized topic pages with examples, key takeaways and XP.
- **Coding challenges** — editor + visible/hidden test cases (levels 6–10);
  execution is intentionally reported as *unavailable* rather than faked.
- **Gamification** — XP with level caps, streaks, seven achievements, daily quest.
- **Demo mode** — a presentation account that survives restarts, no signup needed.
- **Dark/light theme** — animated crossfade, persisted, plus a system option.
- **Responsive navigation** — four bottom tabs plus pushed Topic, Question,
  Daily and Challenge routes.

## Architecture

```
UI layer        ui/            Compose screens, components, theme, design tokens
                navigation/    destinations, routes, NavHost
Domain layer    domain/        models, AdaptiveEngine, DailyPractice, CodeExecutor seam
Data layer      data/          CodeQuestRepository contract,
                               MockCodeQuestRepository (in-memory implementation),
                               MockData + data/questions (authored bank),
                               data/auth (Firebase + demo), data/local (preferences)
```

**Unidirectional flow:** screens are stateless renderers; ViewModels expose
`StateFlow` from the repository; every mutation (answer, lesson, quest) flows through
`MockCodeQuestRepository`, which keeps XP, topics, progress and achievements
consistent across screens. `ServiceLocator` wires the singletons.

### Project structure

```
app/src/main/java/com/codequest/app/
├── MainActivity.kt              # theme + store init, first frame
├── ui/
│   ├── CodeQuestApp.kt          # auth & onboarding gates, scaffold
│   ├── home/ learn/ practice/ daily/ code/ profile/ onboarding/ auth/
│   ├── components/              # GlowCard, GlassButton, progress, entrance motion
│   └── theme/                   # colors, typography, motion, theme blend
├── navigation/                  # 4 tabs + topic/question/daily/challenge routes
├── domain/
│   ├── model/                   # PracticeQuestion, Lesson, LearningProfile, …
│   ├── practice/                # AdaptiveEngine, DailyPractice
│   └── code/                    # CodeExecutor (honest unavailable verdict)
└── data/
    ├── CodeQuestRepository.kt   # contract + ServiceLocator
    ├── MockCodeQuestRepository.kt
    ├── MockData.kt              # lessons, topic model, progress seed
    ├── questions/               # 12 topic files + QuestionBank expansion
    ├── auth/                    # FirebaseAuthRepository, DemoSession
    └── local/LearningStore.kt   # SharedPreferences persistence
app/src/test/java/com/codequest/app/
├── QuestionBankValidationTest.kt  # 12
├── DailyPracticeTest.kt           # 8
├── AdaptiveEngineTest.kt          # 10
└── CodingChallengesTest.kt        # 4
```

## Tech stack

| Component | Version |
|---|---|
| Android Gradle Plugin | 9.3.3 |
| Kotlin | 2.2.10 (bundled with AGP) |
| Kotlin Compose plugin | 2.4.0 |
| Jetpack Compose BOM | 2026.08.00 |
| Navigation Compose | 2.10.2 |
| Lifecycle / ViewModel | 2.11.0 |
| Coroutines | 1.11.0 |
| Firebase BOM *(optional)* | 34.19.0 |
| Credentials Manager / Google ID | 1.6.0 / 1.2.1 |
| JUnit | 4.13.2 |

## Getting started

### Requirements

- Android Studio (latest stable) with JDK 17
- An Android device or emulator on API 24+

### Build & run

```bash
git clone https://github.com/Rickschanchez/Codequest.git
cd Codequest

# debug APK
./gradlew assembleDebug          # Windows: .\gradlew.bat assembleDebug

# install on a connected device
./gradlew installDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`. First launch opens the
offline **demo learner** — no account or network needed.

### Tests

```bash
./gradlew test                   # Windows: .\gradlew.bat test
```

| Suite | Covers | Tests |
|---|---|---|
| `QuestionBankValidationTest` | id uniqueness, structure, answer integrity | 12 |
| `AdaptiveEngineTest` | rolling window, verdict thresholds, level moves | 10 |
| `DailyPracticeTest` | day-key determinism, pinning, level fit | 8 |
| `CodingChallengesTest` | challenge set integrity, XP rewards | 4 |
| **Total** | | **34** |

## Firebase authentication (optional)

`app/google-services.json` is **intentionally not committed** — it contains an API
key. The Gradle build applies the Google Services plugin only when the file exists,
so clones build and run out of the box (demo mode).

To enable Email/Password + Google sign-in:

1. Create a Firebase project and add an Android app with package `com.codequest.app`.
2. Download `google-services.json` into `app/`.
3. Add a **Web** OAuth client (`default_web_client_id`) for Google Sign-In via
   Credential Manager, then rebuild.

Without the file, the app still launches and the sign-in screen degrades to a clear
“Firebase isn’t configured” message instead of crashing.

## Performance notes

Measured on a physical low-end device (Samsung SM-A156E, Android 16) with
`dumpsys gfxinfo` before/after:

| Metric | Before | After |
|---|---|---|
| Practice screen open (cold process) | 22.2 s | **3.3 s** |
| Worst frame during that open | 4,950 ms | **700 ms** |
| Scroll frame time (p50 / p90, warm) | 18 / 22 ms | 17 / 23 ms (parity) |
| Cold start | 1,254 ms | 857–936 ms |

Key changes: the practice list is virtualised with `LazyColumn` (it previously
composed all 1,220 cards up front), bank-wide filters are derived inside
`remember`, the app root collects only the onboarding flag instead of the whole
profile, and per-answer aggregation runs in a single pass.

## Known limitations

- **Session-scoped progression.** Onboarding choices, adaptive levels, the rolling
  answer window, the pinned daily set, appearance and the demo session persist to
  disk. XP, solved IDs, lesson completion, streaks and achievements are reseeded
  from `MockData` on each process start (there is no backend to store them).
- **No code execution.** `CodeExecutor` never fabricates a verdict — it reports
  `Unavailable` with the test cases to check against. The seam is ready for a real
  sandboxed runner.
- **Google Sign-In** additionally requires a Web OAuth client in your own
  Firebase/Google Cloud project (the repo ships no credentials).
- **Content is local.** The question bank, lessons and challenges are authored in
  this repository; nothing is fetched from a server.

## Repository notes

Ignored on purpose: build outputs, Gradle/IDE state, `local.properties`,
`google-services*.json` (API key), keystores, and an unrelated
`Policy-Gated-Enterprise-Execution-System/` folder that lives next to the project.
