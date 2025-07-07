# ApiHit

ApiHit is a news Android project that fetches the latest Indian news from newsapi.org. It is built using Java and XML.

## Features

- **News Fetching:** Retrieves the latest news articles from newsapi.org and displays them in a modern UI.
- **User Authentication:**
  - Email/password registration and login using Firebase Authentication.
  - Google Sign-In support for quick login.
- **Student Management:**
  - Add student records (name, email, age) via a form.
  - Student data is stored in Firebase Firestore.
- **Room Database Caching:**
  - News articles are cached locally using Room database for offline access and faster loading.
- **Modern UI:**
  - Multiple screens: Login, Register, Main, News Channel.
  - Custom backgrounds and button styles.
- **Error Handling:**
  - Handles API and Firestore errors gracefully with user feedback.

## Main Components

- `MainActivity.java`: Handles user input and navigation.
- `Loginscreen.java`: Login screen with Firebase Auth and Google Sign-In.
- `RegisterActivity.java`: User registration with Firebase Auth.
- `NewsChannel.java`: Displays news articles and manages Room DB caching.
- `StudentRepository.java`: Manages student data and Firestore integration.

## Getting Started

1. Clone the repository.
2. Add your `google-services.json` to the `app/` directory.
3. Build and run the app on an Android device or emulator.

---

Feel free to contribute or open issues for improvements!
