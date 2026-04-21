## Reflect: Nature-Focused Private Diary
Most Diary/Note-Taking apps are cluttered with features that distract from the actual act of writing. 
**But It** is built to be a "quiet corner" on your device—a minimalist and secure place for daily use.

## Why this project is useful
This app removes the friction of complex UIs and replaces it with a calming aesthetic and a "privacy-first" approach.
It’s perfect for anyone who wants a digital diary that feels as safe and personal as a physical one.

## What you can do with this project
- **Secure your thoughts:** Every entry is locked behind Android's Biometric API (Fingerprint/Face/PIN).
- **Mood Tracking:** Label entries with moods to see your emotional patterns over time.
- **Responsive Writing:** The UI intelligently adapts. Use it in portrait for a quick thought, or flip to landscape for a focused, distraction-free writing session (where the header image hides to maximize space).
- **Learn & Extend:** If you're a developer, this is a clean implementation of:
    - **Jetpack Compose** for modern UI.
    - **Room Database** for offline-first persistence.
    - **MVVM Architecture** for scalable code.
    - **Biometric API** integration.

## How to use it
### As a user:
1.  **Authenticate:** Open the app and use your device's security (fingerprint/PIN) to enter.
2.  **Diary:** Tap the floating '+' button to write a new entry. Pick a mood that fits your day.
3.  **Review:** Scroll through your timeline. Use the "Show More" toggle to read longer entries without leaving the main screen.
4.  **Edit/Delete:** Use the menu on any card to update or remove old entries.
5.  Some ScreenShots will describe more, I think ->
   ![Image Alt](https://github.com/shuvo3335/Secure-Diary-App/blob/main/Emulator_Screenshot_20260421_234549.png)![Image Alt](https://github.com/shuvo3335/Secure-Diary-App/blob/main/Emulator_Screenshot_20260421_234711.png) ![Image Alt](https://github.com/shuvo3335/Secure-Diary-App/blob/main/Emulator_Screenshot_20260421_234725.png)![Image Alt](https://github.com/shuvo3335/Secure-Diary-App/blob/main/Emulator_Screenshot_20260421_234742.png)
    ![Image Alt](https://github.com/shuvo3335/Secure-Diary-App/blob/main/Emulator_Screenshot_20260421_234816.png)
  
### As a developer:
1.  Clone the repository.
2.  Open in **Android Studio (Ladybug or newer)**.
3.  Build and run on any device (API 24+). 
*Note: Biometric features require a device with a set screen lock.*
*Built with Kotlin, Jetpack Compose, and a focus on UX.*
