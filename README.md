# STracker — Strength & Progression Tracker

**STracker** is a modern, high-performance Android application designed for serious strength athletes. It focuses on tracking workout data and providing intelligent load progression recommendations based on scientifically proven formulas.

Built with **Jetpack Compose** and **Clean Architecture**, STracker offers a seamless and powerful experience for anyone looking to optimize their strength training.

---

## 🚀 Key Features

- **Intuitive Workout Logging**: Record sets, weights, repetitions, and RPE (Rate of Perceived Exertion) with minimal friction.
- **Intelligent Progression Advice**: The app analyzes your previous performances and suggests the optimal weight and rep range for your next session.
- **Exercise Library**: Manage a comprehensive list of exercises, categorized by muscle groups and movement types.
- **Historical Analytics**: Track your progress over time with detailed history of every workout and exercise.
- **Estimated 1RM Tracking**: Automatically calculates your estimated One-Rep Max using Epley and Brzycki formulas to visualize strength gains.
- **Auto-Fill & Smart Templates**: Quickly start workouts with pre-filled data from your last successful session.

## 🧠 The Science of Progression

STracker doesn't just store data; it interprets it. Using the **Epley** and **Brzycki** formulas, the app calculates your **e1RM** (Estimated One-Rep Max) to identify trends:

- **Growth Trend**: Suggests a weight increase (+2.5kg for compounds, +1.25kg for isolation).
- **Plateau Detection**: Recommends increasing repetitions or a minor "deload" if progress stalls.
- **Fatigue Management**: Monitors high RPE levels and suggests backing off when necessary to prevent overtraining.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Clean Architecture (Domain, Data, Presentation layers)
- **Asynchronous Flow**: Kotlin Coroutines & Flow
- **Dependency Injection**: Hilt
- **Local Storage**: Room (SQLite)
- **Preferences**: DataStore
- **Date/Time**: Kotlinx Datetime

## 🏗 Project Structure

The project follows strict Clean Architecture principles to ensure maintainability and testability:

```text
app/src/main/java/com/example/stracker/
├── data/           # Repository implementations, Room DB, entities, mappers
├── di/             # Hilt modules (Database, Repository, UseCase)
├── domain/         # Business logic: models, repository interfaces, use cases
├── presentation/   # UI layer: Compose Screens, ViewModels, UI State
└── ui/             # Theme and styling (Colors, Typography, Shapes)
```

## 🚦 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 34+
- Java 11+

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/sandexzx/STracker.git
   ```
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run the app on an emulator or physical device (Min SDK: 34).

## 📄 Documentation

For more detailed information regarding the logic and specifications, please refer to:
- [Technical Specification (RU)](SPECIFICATION.md)
- [Project Goals & Problems (RU)](PROBLEM.md)

---

Developed as a modern solution for strength training enthusiasts.

---

# STracker — Трекер силы и прогрессии (RU)

**STracker** — это современное Android-приложение для силовых тренировок. Основной фокус сделан на анализе прогресса и выдаче рекомендаций по нагрузке на основе спортивной науки.

## 🚀 Основные возможности

- **Удобный лог тренировок**: Запись весов, повторений, подходов и RPE.
- **Умные рекомендации**: Приложение анализирует ваши прошлые результаты и подсказывает оптимальный вес на следующую тренировку.
- **Библиотека упражнений**: Удобное управление списком упражнений по группам мышц.
- **Аналитика прогресса**: Отслеживание расчетного одноповторного максимума (e1RM) по формулам Эпли и Бржицки.
- **Автозаполнение**: Быстрый старт на основе данных из последней успешной тренировки.

## 🛠 Технологический стек

- **Язык**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Архитектура**: Clean Architecture (MVVM)
- **База данных**: Room
- **DI**: Hilt
- **Состояние**: Flow & Coroutines

## 🏗 Структура проекта

Проект разделен на слои:
- `domain`: Бизнес-логика, модели и интерфейсы репозиториев (не зависит от библиотек).
- `data`: Реализация репозиториев, работа с Room DB и DataStore.
- `presentation`: UI-слой на Compose, ViewModels и управление состоянием.
- `di`: Конфигурация зависимостей Hilt.

## 🏁 Как запустить

1. Установите Android Studio (Ladybug или новее).
2. Склонируйте репозиторий.
3. Дождитесь синхронизации Gradle.
4. Запустите на устройстве или эмуляторе (Android 14+ / SDK 34).
