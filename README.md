# SurgiCare 🏥
> *Caring for You, Virtually*

SurgiCare is an Android mobile application designed to revolutionize post-surgery patient monitoring. It enables healthcare professionals to remotely track patients' vital signs in real time, replacing traditional paper-based systems with a seamless digital solution that covers all three critical recovery phases: ICU, post-ICU observation, and home-based monitoring.

**Bachelor's Thesis Project — German Jordanian University, October 2024**
Supervised by Dr. Samer Nofal

---

## Features

- **Real-Time Vitals Monitoring** — Track heart rate, blood pressure, oxygen saturation (SpO2), respiratory rate, temperature, blood sugar, weight, and blood group
- **Data Visualization** — Interactive bar charts displaying the last 5 readings per metric, helping providers spot trends and anomalies quickly
- **Smart Notifications** — Scheduled medication reminders and health check-in alerts powered by AlarmManager and WorkManager, delivered even when the app is closed
- **In-App Chat** — Real-time messaging between patients and healthcare providers via Firebase-backed ChatRooms
- **Google Sign-In & Registration** — Secure authentication via Firebase Auth with Google OAuth and email/password support
- **Role-Based Access** — Doctors, nurses, and patients each have scoped access to relevant data

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM (ViewModel + ViewModelFactory) |
| Database | Firebase Firestore (NoSQL, real-time) |
| Authentication | Firebase Auth + Google Sign-In |
| Notifications | AlarmManager + NotificationManager + WorkManager |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Build System | Gradle (Kotlin DSL) |

---

## Architecture

SurgiCare follows the **MVVM** pattern with a clean separation between UI, business logic, and data layers.
```
com.example.surgicare/
├── SignIn/                  # Auth, registration, Google login, user data models
├── vitals/                  # Vitals logging screen, ViewModel, data model
├── DataVisualization/       # Bar charts and recovery trend screens
├── Notifications/           # NotificationWorker, NotificationHelper, NotificationScreen
└── MainActivity.kt          # App entry point and navigation
```

---

## Firestore Database Structure
```
Firestore
├── users/                   # All users (doctors, nurses, patients)
├── patients/
│   └── {userId}/
│       └── latestVitals/    # Most recent vital signs
├── vitalsHistory/           # Historical vitals records for trend analysis
├── Appointments/            # Scheduled and completed appointments
├── ChatRooms/
│   └── {chatRoomId}/
│       └── Messages/        # Individual chat messages
└── notifications/           # Notification records
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- A Firebase project with the following enabled:
  - Firestore Database
  - Firebase Authentication (Email/Password + Google)
  - Firebase Cloud Messaging

### Setup

1. Clone the repository:
```bash
   git clone https://github.com/anasaljboor/SurgiCare.git
   cd SurgiCare
```

2. Open the project in Android Studio.

3. Add your `google-services.json` to the `app/` directory (download from your Firebase Console).

4. Build and run on an emulator or physical device (API 26+).

---

## Screens

| Screen | Description |
|---|---|
| Sign-In / Register | Firebase authentication with Google Sign-In support |
| Vitals | Real-time display of all patient vital signs |
| Data Visualization | Bar charts of the last 5 readings per metric |
| Notifications | Feed of all alerts (high temperature, abnormal heart rate, etc.) |
| Chat | Real-time messaging between patient and medical staff |

---

## Branches

| Branch | Description |
|---|---|
| `master` | Stable, latest merged code |
| `Notifications` | Notification system implementation |
| `vitals` | Vitals tracking feature |

---

## Limitations & Future Work

Current limitations include no wearable device integration, limited offline functionality, and no EHR system integration. Planned future enhancements:

- AI-based predictive health alerts and anomaly detection
- Integration with hospital monitoring devices and smartwatches
- Video consultation feature for remote doctor check-ups
- Electronic Health Record (EHR) system integration
- Advanced customizable reporting for healthcare providers

---

## Author

**Anas Ahmad Al-Jboor**
German Jordanian University — BSc Computer Science, 2024
[github.com/anasaljboor](https://github.com/anasaljboor)
