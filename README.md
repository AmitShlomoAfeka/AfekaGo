# 🚗 AfekaGo - אפליקציית שיתוף נסיעות לסטודנטים באפקה

**AfekaGo** היא אפליקציית מוקאפ לאנדרואיד שנבנתה כדי לאפשר לסטודנטים במכללת אפקה לתאם נסיעות שיתופיות ביניהם. האפליקציה כוללת מערכת זיהוי משתמשים מבוססת Firebase, מפה דינאמית עם Google Maps API, תכנון נסיעות, צפייה בנהגים/נוסעים קרובים, והדמיית תשלום.

---

## 🎯 תכונות עיקריות

- 🔐 כניסה מאובטחת עם Firebase Authentication
- 🧑‍🎓 התחברות רק לסטודנטים מאומתים במכללת אפקה
- 🗺️ מפה אינטראקטיבית המציגה נהגים/נוסעים קרובים
- 👥 החלפה בין מצב "נהג" ל"נוסע"
- 📆 תכנון נסיעה עתידית
- 📍 התחלת נסיעה מתוך רשימת ההתאמות
- 💳 מסך תשלום (מוקאפ בלבד)
- 📑 מסך הגדרות, התנתקות וניווט

---

## ⚠️ הבהרות חשובות

> 🧪 **מדובר באפליקציית מוקאפ בלבד!**

- חיבור לתשלום אינו מתבצע בפועל – רק סימולציה.
- האפליקציה מיועדת לשימוש בלעדי של סטודנטים מאומתים באפקה (נדרש אימייל מוסדי).
- אין מנגנון התאמה אוטומטית בין נהג לנוסע – ההתאמה נעשית באופן סימבולי לצורך הדגמה.

---

## 🛠️ טכנולוגיות בשימוש

- Kotlin + Android Studio
- Jetpack Compose
- Firebase (Authentication + Realtime Database)
- Google Maps SDK for Android
- MVVM architecture
- Material 3

---

## 📁 מבנה הפרויקט

```bash
AfekaGo/
│
├── app/
│   ├── build.gradle                  # הגדרות build לאפליקציה
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # הרשאות והגדרות פעילות
│           ├── java/com/example/afekago/
│           │   ├── MainActivity.kt              # הפעילות הראשית עם המפה והסרגלים
│           │   ├── PlanTripActivity.kt          # מסך לתכנון נסיעה עתידית
│           │   ├── SettingsActivity.kt          # הגדרות משתמש
│           │   ├── NavigationActivity.kt        # ניווט נסיעה בין נהג לנוסע
│           │   ├── TripsAdapter.kt              # מתאם לרשימת נסיעות מתוכננות
│           │   ├── screens/
│           │   │   └── DriverViewModel.kt       # ניהול בקשות נסיעה לפי MVVM
│           │   ├── models/
│           │   │   ├── User.kt                  # מבנה נתונים של משתמש
│           │   │   ├── RideRequest.kt           # מבנה של בקשת נסיעה
│           │   └── utils/
│           │       ├── findNearbyUsers.kt       # פונקציית שליפת משתמשים קרובים
│           │       ├── getCurrentUserData.kt    # שליפת מידע על המשתמש המחובר
│           │       ├── sendRideRequest.kt       # שליחת בקשת נסיעה
│           │       └── updateRideRequestStatus.kt # עדכון סטטוס בקשה
│           │
│           └── res/
│               ├── layout/                      # אם יש Layoutים נוספים
│               ├── drawable/                    # אייקונים: ic_menu, ic_logout, ic_calendar, וכו'
│               ├── values/
│               │   ├── colors.xml               # צבעים מותאמים אישית
│               │   ├── strings.xml              # טקסטים קבועים
│               │   └── themes.xml               # ערכת העיצוב של האפליקציה
│               └── mipmap/                      # לוגואים ואייקון האפליקציה
│
├── build.gradle
├── README.md
└── presentation/
    └── AfekaGo_206994824_Amit_Shlomo.pptx       # מצגת הסבר עם תיעוד האפליקציה וסרטוני הדגמה

[AfekaGo_206994824_Amit_Shlomo.pptx](https://github.com/user-attachments/files/19405950/AfekaGo_206994824_Amit_Shlomo.pptx)

