# 📱 Event Reminder Pro

A powerful and modern Android application built to simplify your day-to-day life by managing reminders, tracking world time zones, and delivering real-time weather updates – all in one place!

---

## 🚀 High-Level Overview

**Event Reminder Pro** is a feature-rich Android app that helps users:
- Create and manage daily and event-based reminders
- Track multiple global time zones with a real-time world clock
- View live weather based on their current location
- Enjoy a polished UI experience with dark mode, Material Design, and custom dialogs

---

## 🧠 Key Features

### ✅ 1. Reminders & Event Management
- Set reminders with custom titles, descriptions, and trigger times
- Support for one-time and recurring events (e.g., birthdays, meetings, medications)
- Mark reminders as completed
- **Alarm rings and notifies exactly at the set time** with sound + notification
- Uses Room DB for storage and WorkManager for exact triggering

### 🌍 2. World Clock
- Add clocks for multiple cities (e.g., New York, Tokyo, Sydney)
- Displays real-time updates with accurate timezone handling
- Drag-and-drop reordering, favorite cities support
- Search bar to find and add any global city

### 🌦️ 3. Weather Integration
- Automatically fetches weather using the **OpenWeatherMap API**
- Live temperature, city, weather condition
- Refreshes every 5 minutes or manually
- Dynamic icons for weather states (rain, sun, clouds, etc.)

### 📍 4. Location Awareness
- Uses Google Play Location Services for GPS access
- Required to show local weather and city name
- Asks permission at first launch

### ⏱️ 5. Stopwatch
- Simple and effective stopwatch utility built-in
- Start, pause, reset, and track time

### 🎨 6. Modern UI/UX
- Built with **Material Components**: FAB, CardView, Dialogs
- Gradient backgrounds and dark theme support
- Intuitive navigation and friendly experience

### 🔔 7. Notifications
- Reminder alerts using Android notification manager
- Trigger ringtone and vibration (if allowed)
- Doesn’t repeat automatically unless recurrence is set

---

## 🔄 Workflow Summary

1. App launches with splash screen  
2. Displays reminders, weather, and clocks on the main dashboard  
3. User can add/edit/delete reminders  
4. Reminder triggers at exact time with alert  
5. World clock updates every minute in the background  
6. Weather card refreshes automatically  
7. Location requested once on first launch  

---

## ⚙️ Tech Stack & Libraries

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack** | ViewModel, LiveData, Navigation |
| **Room** | Local DB for reminders |
| **WorkManager** | Alarm scheduling |
| **Retrofit2 + Gson** | API calls |
| **OpenWeatherMap API** | Live weather data |
| **Google Location API** | Fetch current location |
| **Dagger Hilt** | Dependency Injection |
| **Jetpack Compose** | Used in selected components |
| **Material Design** | UI components |

---

## 📊 Project Summary Table

| Feature              | Description |
|----------------------|-------------|
| Reminders            | Create, edit, delete, repeat, mark done |
| World Clock          | Add, remove, reorder cities; live updates |
| Weather Integration  | Live data from current GPS location |
| Location Awareness   | One-time permission request for location |
| Notifications        | Alarm triggers at set time with sound |
| Stopwatch            | Simple stopwatch utility |
| UI/UX                | Gradient cards, dark mode, responsive UI |

---



### 📸 Screenshots (optional)
| Splash Screen | Create Reminder | Home  |  World Clock |
|--------------|------------|------------|------------|
| <img src="https://github.com/user-attachments/assets/e11f160a-0ffe-45ef-8c3a-01f61f68fed3" width="350" height="400"> | <img src="https://github.com/user-attachments/assets/0eb0c980-2836-4b77-8d2e-cc2e43ed582e" width="350" height="400"> | <img src="https://github.com/user-attachments/assets/623986d3-106f-4b91-91f2-01d0e7e48efa" width="330" height="380"> | <img src="https://github.com/user-attachments/assets/25411333-5524-49d5-bc78-f0e5dd7eed3a" width="350" height="400"> |


##  StopWatch
<img src="https://github.com/user-attachments/assets/c7f7ce55-6ade-4c43-8314-35a3da49fdce" width="350" height="400">


---
## 📥 Download APK

> Click below to download and try the app:

[🔗 Download EventReminderPro APK](https://drive.google.com/file/d/1abcXYZ/view?usp=sharing)


## 📥 Installation Guide

1. Clone the repository:
```bash
https://github.com/Rajcharchil/EventReminderPro/
