# 🍽️ Meal Tracker App

The **Meal Tracker App** is an Android-based nutrition logging application that helps users track their daily meals and monitor nutrient intake. It integrates with a comprehensive food nutrition database (`foodnutrients.csv`) to provide detailed macro and micronutrient information.

## 📱 Features

- ✅ Log meals with food name, quantity, and time
- 🔍 Search nutrition facts from an offline CSV database
- 📊 Get detailed breakdown of calories, proteins, carbs, and fats
- 🧠 Simple, intuitive UI built with Android Studio
- 📁 Local data persistence using SQLite (or Room DB)

---

## 📂 Project Structure

```

Meal\_Tracker\_App/
├── app/                     # Main Android app source
├── build.gradle             # Project build file
├── gradlew                  # Gradle wrapper
├── .gitignore               # Git ignored files
└── local.properties         # Local SDK paths

nutritiondatabase/
└── foodnutrients.csv        # Nutrition facts dataset

````

---

## 📊 Nutrition Database

The app uses the `foodnutrients.csv` file, which contains:

- Food items (name, brand)
- Macronutrients: Calories, Protein, Carbs, Fats
- Micronutrients (optional)

**Sample Fields:**
- `Food_Name`
- `Calories`
- `Protein_g`
- `Carbohydrates_g`
- `Fat_g`

---

## 🚀 Getting Started

### 🔧 Prerequisites

- Android Studio (Electric Eel or newer)
- Android SDK 29+
- Gradle 7+

### 🛠️ Setup

1. **Clone the repo:**
    ```bash
    git clone https://github.com/Anshul-ydv/meal-tracker-app.git
    cd meal-tracker-app
    ```

2. **Open `Meal_Tracker_App` in Android Studio**

3. **Add nutrition database:**
    - Place `foodnutrients.csv` inside the `assets` or `res/raw` directory of your Android project.

4. **Build & Run the app** on emulator or real device.

---

## 💡 How it Works

- User logs a meal → selects food item → app fetches nutrition data from `foodnutrients.csv`
- Calculations done in-app for totals per day/week
- All meal entries saved locally

---

## 🧪 Demo & Screenshots

> _Screenshots/GIFs can go here once added_

---

## 🙌 Contributing

Feel free to fork this repo and contribute!

```bash
git checkout -b feature/new-feature
git commit -m "Add new feature"
git push origin feature/new-feature
````

---

## 📄 License

MIT License © 2025
