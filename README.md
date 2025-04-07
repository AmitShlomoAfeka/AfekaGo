<h1 align="center">🚗 AfekaGo</h1>
<h3 align="center">אפליקציית שיתוף נסיעות לסטודנטים באפקה</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-Android-blueviolet" />
  <img src="https://img.shields.io/badge/Firebase-RealtimeDB-orange" />
  <img src="https://img.shields.io/badge/Mockup-App-yellow" />
</p>

---

## 🧭 על הפרויקט

**AfekaGo** היא אפליקציית *מוקאפ* לאנדרואיד שפותחה לצורך תיאום נסיעות שיתופיות בין סטודנטים באפקה. האפליקציה מאפשרת צפייה בנהגים/נוסעים קרובים, תכנון נסיעה עתידית, הדמיית תשלום וניווט — הכל עם ממשק מבוסס Firebase, Google Maps ו־Jetpack Compose.

---

## ✨ תכונות עיקריות

| תכונה | תיאור |
|-------|--------|
| 🔐 כניסה מאובטחת | Firebase Authentication |
| 🎓 אימות סטודנט | התחברות רק באמצעות מייל מוסדי של אפקה |
| 🗺️ מפה אינטראקטיבית | Google Maps המציג נהגים/נוסעים בקרבת מקום |
| 🚘 מעבר בין תפקידים | בחירה בין מצב "נהג" ל"נוסע" |
| 📆 תכנון נסיעה עתידית | כולל בחירת תאריך ושעה |
| 📍 התחלת נסיעה | מתוך רשימת ההתאמות |
| 💳 מסך תשלום | הדמיית תשלום (מוקאפ בלבד) |
| ⚙️ מסך הגדרות | ניהול פרופיל, התנתקות וניווט |

---

## ⚠️ הבהרות חשובות

> 🧪 **האפליקציה היא לצורכי הדגמה בלבד (Mockup)!**

- ❌ אין תשלום אמיתי – מדובר בסימולציה בלבד  
- ✅ גישה רק לסטודנטים עם אימייל מוסדי של אפקה  
- 🤝 ההתאמה בין נהג לנוסע אינה אוטומטית – היא ידנית לצורך הדגמה  

---

## 🛠️ טכנולוגיות בשימוש

- **Kotlin** + Android Studio  
- **Jetpack Compose**  
- **Firebase** (Authentication + Realtime Database)  
- **Google Maps SDK for Android**  
- **Material Design 3**  
- **MVVM Architecture**

---

## 📁 מבנה הפרויקט

```bash
AfekaGo/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/afekago/
│           │   ├── MainActivity.kt
│           │   ├── PlanTripActivity.kt
│           │   ├── SettingsActivity.kt
│           │   ├── NavigationActivity.kt
│           │   ├── TripsAdapter.kt
│           │   ├── screens/
│           │   │   └── DriverViewModel.kt
│           │   ├── models/
│           │   │   ├── User.kt
│           │   │   └── RideRequest.kt
│           │   └── utils/
│           │       ├── findNearbyUsers.kt
│           │       ├── getCurrentUserData.kt
│           │       ├── sendRideRequest.kt
│           │       └── updateRideRequestStatus.kt
│           └── res/
│               ├── layout/
│               ├── drawable/
│               ├── values/
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── mipmap/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── gradle.properties
├── README.md
└── presentation/
    └── AfekaGo_206994824_Amit_Shlomo.pptx
