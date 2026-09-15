# MindLoop 🔁

> **Cognitive Spaced Repetition & Video Learning Platform for Competitive Exam Preparation**

MindLoop is a modern, high-performance Android application built with **Jetpack Compose**, **Kotlin Coroutines**, and **Material Design 3**. It leverages the **SuperMemo-2 (SM-2) / Ebbinghaus Spaced Repetition System (SRS)** to ensure high memory retention by automatically prioritizing difficult concepts, mistake remediation, and active recall.

---

## 📲 How to Download the App (APK)

You have three easy ways to download and run the APK on your Android device:

### Option 1: Download from GitHub Actions (Automated Builds)
Every time code is pushed, GitHub automatically compiles the latest version of the app:
1. Open this repository on GitHub (`https://github.com/narayanrajput5206/mindloop`).
2. Click on the **Actions** tab at the top of the repository.
3. Click the most recent workflow run (titled **"Build and Release Android APK"**).
4. Scroll down to the bottom of the page to the **Artifacts** section.
5. Click **`mindloop-app-debug`** to download the ZIP file containing `app-debug.apk`.
6. Extract the ZIP and transfer/install `app-debug.apk` onto your Android phone!

> **Tip**: You can also trigger a new APK build at any time by going to **Actions** -> **Build and Release Android APK** -> click **Run workflow**.

---

### Option 2: Direct Download from Google AI Studio
If you are working in Google AI Studio:
1. In the top toolbar, click the **Settings / More Options** menu (three dots or gear icon).
2. Look for **Export / Download**.
3. Select **Download APK**. The pre-compiled APK (`app-debug.apk`) will download directly to your computer or phone.

---

### Option 3: Build Locally via Command Line / Android Studio
If you want to compile the project locally on your machine:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/narayanrajput5206/mindloop.git
   cd mindloop
   ```

2. **Build the Debug APK**:
   ```bash
   # Using Gradle 9+ and Java 21:
   gradle assembleDebug
   ```

3. **Locate your APK**:
   The compiled APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Install onto a connected Android device**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📥 How to Install the APK on Your Android Device

1. Once downloaded, open the `app-debug.apk` file from your device's **Downloads** folder or notification shade.
2. If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*:
   - Tap **Settings**.
   - Enable **Allow from this source**.
3. Tap **Install** and wait for installation to complete.
4. Tap **Open** to launch MindLoop!

---

## 🚀 Key Features

- 🧠 **Spaced Repetition System (SRS)**:
  - Dynamically calculates priority scores using mistake frequencies, error rates, and user recall ratings (`HARD`, `MEDIUM`, `EASY`).
  - Exponential decay forgetting curve ($R = e^{-t / S}$) estimates memory retention and schedules reviews before concepts are forgotten.
  - Automatically pushes difficult and mistake-prone questions to the front of the review queue.

- 🎬 **Reels Learning Concepts**:
  - Educational short-form video feed for rapid visual learning.
  - Seamlessly linked with quizzes, conceptual flashcards, and chapter summaries.

- ❌ **Comprehensive Mistakes Log**:
  - Dedicated mistake tracking organized by Exam (UPSI, etc.) and Subject.
  - Detailed drill-down by chapter, enabling users to re-test erroneous questions or watch the linked concept reel with one tap.

- 📝 **Handwritten-Style Notes & Visual Sketchnotes**:
  - Rich study materials styled like handwritten notebooks for pleasant, eye-safe reading.
  - Quick transitions between study notes, video reels, and testing modes.

- 📊 **Progress & Analytics Dashboard**:
  - Real-time study streak, daily questions completed, accuracy percentages, and average time per question.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) with StateFlow & Coroutines
- **Database & Backend**: Room Database (Local Persistence) & Firebase / Firestore Sync
- **Build System**: Gradle 9.3 (Kotlin DSL) with Version Catalog (`libs.versions.toml`)
- **Target SDK**: Android 35 (VanillaIceCream)
- **Min SDK**: Android 26 (Oreo)

---

## 🔄 Syncing Changes to GitHub

To push future updates from Google AI Studio to your GitHub repository:
1. In the top-right corner of Google AI Studio, click the **Export** / **GitHub** button.
2. Select **Push to GitHub** (or select the `mindloop` repository).
3. Confirm the push. Your GitHub repository will instantly be updated with the latest code, and the GitHub Actions workflow will automatically compile a fresh APK!
