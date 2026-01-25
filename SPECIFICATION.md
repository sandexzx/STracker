# STracker — Техническое задание

## 1. Общее описание

**STracker** — персональное Android-приложение для отслеживания силовых тренировок. Основная цель — фиксировать выполненные упражнения с параметрами (вес, подходы, повторения), показывать результаты предыдущих тренировок и рекомендовать прогрессию нагрузки.

---

## 2. Ключевые функции

### 2.1 Управление тренировками

| Функция | Описание |
|---------|----------|
| **Создание тренировки** | Начало новой тренировочной сессии с датой/временем |
| **Добавление упражнений** | Выбор из библиотеки или создание нового упражнения |
| **Запись подходов** | Для каждого упражнения: вес (кг), повторения, опционально — RPE (Rate of Perceived Exertion, 1-10) |
| **Завершение тренировки** | Сохранение сессии с возможностью добавить заметку |
| **Редактирование** | Возможность изменить/удалить подход или упражнение |

### 2.2 История и аналитика

| Функция | Описание |
|---------|----------|
| **История тренировок** | Список всех прошлых сессий с группировкой по дням/неделям |
| **Детали тренировки** | Просмотр всех упражнений и подходов конкретной сессии |
| **История упражнения** | Все выполнения конкретного упражнения с прогрессом |
| **Прогресс по упражнению** | График 1RM (расчётный одноповторный максимум) во времени |

### 2.3 Рекомендации по прогрессии

| Функция | Описание |
|---------|----------|
| **Автозаполнение** | При добавлении упражнения — предзаполнение весом/повторениями из последней тренировки |
| **Рекомендация веса** | Алгоритм подсказывает следующий вес на основе прогресса |
| **Индикатор готовности** | Визуальный сигнал: «можно прибавить» / «закрепить результат» / «снизить нагрузку» |

---

## 3. Алгоритм прогрессии

### 3.1 Расчёт 1RM (Estimated One-Rep Max)

Используем формулу **Epley**:
```
1RM = weight × (1 + reps / 30)
```

Альтернатива — **Brzycki**:
```
1RM = weight × (36 / (37 - reps))
```

> Для подходов с повторениями > 12 точность снижается, но для трекинга прогресса достаточно.

### 3.2 Логика рекомендаций

```
┌─────────────────────────────────────────────────────────────────────┐
│                      АЛГОРИТМ ПРОГРЕССИИ                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. Получить последние N тренировок с этим упражнением (N = 3-5)   │
│                                                                     │
│  2. Рассчитать средний e1RM за последние сессии                    │
│                                                                     │
│  3. Определить тренд:                                              │
│     • Рост e1RM ≥ 2.5% за последние 3 сессии → ПРОГРЕССИЯ          │
│     • e1RM стабилен (±2.5%) → ПЛАТО                                │
│     • e1RM падает > 5% → РЕГРЕССИЯ / УСТАЛОСТЬ                     │
│                                                                     │
│  4. Рекомендация:                                                  │
│     ┌──────────────┬────────────────────────────────────────────┐  │
│     │ ПРОГРЕССИЯ   │ +2.5 кг к рабочему весу                    │  │
│     │              │ ИЛИ +1-2 повторения при том же весе        │  │
│     ├──────────────┼────────────────────────────────────────────┤  │
│     │ ПЛАТО        │ Сохранить вес, попробовать +1 повтор       │  │
│     │              │ Альтернатива: deload -10% на 1 неделю      │  │
│     ├──────────────┼────────────────────────────────────────────┤  │
│     │ РЕГРЕССИЯ    │ Снизить вес на 10%                         │  │
│     │              │ Проверить восстановление (сон, питание)    │  │
│     └──────────────┴────────────────────────────────────────────┘  │
│                                                                     │
│  5. Ограничения безопасности:                                      │
│     • Максимальный прирост за сессию: +5 кг (базовые) / +2.5 кг    │
│     • Если RPE последнего подхода ≥ 9 → не рекомендовать прибавку  │
│     • После перерыва > 14 дней → deload -20%                       │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3 Категории упражнений для прогрессии

| Категория | Примеры | Шаг прогрессии |
|-----------|---------|----------------|
| **Базовые** | Присед, становая, жим лёжа | 2.5 кг |
| **Вспомогательные** | Тяга штанги, жим стоя | 1.25–2.5 кг |
| **Изолирующие** | Бицепс, трицепс, икры | 1.25 кг или +2 повтора |

---

## 4. Модель данных

### 4.1 ER-диаграмма (упрощённо)

```
┌─────────────────┐       ┌─────────────────────┐       ┌─────────────────┐
│    Exercise     │       │   WorkoutExercise   │       │     Workout     │
├─────────────────┤       ├─────────────────────┤       ├─────────────────┤
│ id: Long (PK)   │       │ id: Long (PK)       │       │ id: Long (PK)   │
│ name: String    │◄──────│ exerciseId: Long(FK)│──────►│ startedAt: Long │
│ category: Enum  │       │ workoutId: Long(FK) │       │ finishedAt:Long?│
│ muscleGroup:Enum│       │ order: Int          │       │ note: String?   │
│ notes: String?  │       └─────────┬───────────┘       │ isCompleted:Bool│
└─────────────────┘                 │                   └─────────────────┘
                                    │
                                    ▼
                          ┌─────────────────────┐
                          │        Set          │
                          ├─────────────────────┤
                          │ id: Long (PK)       │
                          │ workoutExerciseId:FK│
                          │ setNumber: Int      │
                          │ weight: Float       │
                          │ reps: Int           │
                          │ rpe: Int? (1-10)    │
                          │ isCompleted: Bool   │
                          │ isWarmup: Bool      │
                          └─────────────────────┘
```

### 4.2 Kotlin-модели (Domain Layer)

```kotlin
// Мышечные группы
enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, 
    QUADRICEPS, HAMSTRINGS, GLUTES, CALVES, 
    ABS, FOREARMS, FULL_BODY
}

// Категория упражнения (влияет на прогрессию)
enum class ExerciseCategory {
    COMPOUND,    // Базовые многосуставные
    ACCESSORY,   // Вспомогательные
    ISOLATION    // Изолирующие
}

// Упражнение
data class Exercise(
    val id: Long = 0,
    val name: String,
    val category: ExerciseCategory,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val notes: String? = null
)

// Подход
data class ExerciseSet(
    val id: Long = 0,
    val setNumber: Int,
    val weight: Float,         // кг
    val reps: Int,
    val rpe: Int? = null,      // 1-10, опционально
    val isCompleted: Boolean = false,
    val isWarmup: Boolean = false
)

// Упражнение в тренировке (с подходами)
data class WorkoutExercise(
    val id: Long = 0,
    val exercise: Exercise,
    val sets: List<ExerciseSet>,
    val order: Int,
    val restSeconds: Int? = null  // Таймер отдыха (опционально)
)

// Тренировочная сессия
data class Workout(
    val id: Long = 0,
    val startedAt: Instant,
    val finishedAt: Instant? = null,
    val exercises: List<WorkoutExercise> = emptyList(),
    val note: String? = null,
    val isCompleted: Boolean = false
)

// Рекомендация по прогрессии
data class ProgressionAdvice(
    val recommendedWeight: Float,
    val recommendedReps: Int,
    val trend: ProgressionTrend,
    val message: String
)

enum class ProgressionTrend {
    PROGRESSING,    // Можно прибавлять
    PLATEAU,        // Стабильно, закрепляем
    REGRESSING,     // Снижение, нужен отдых/deload
    FIRST_TIME      // Первое выполнение упражнения
}
```

---

## 5. Архитектура приложения

### 5.1 Структура модулей (пакетов)

```
com.example.stracker/
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── STrackerDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── ExerciseDao.kt
│   │   │   │   ├── WorkoutDao.kt
│   │   │   │   └── SetDao.kt
│   │   │   └── entity/
│   │   │       ├── ExerciseEntity.kt
│   │   │       ├── WorkoutEntity.kt
│   │   │       ├── WorkoutExerciseEntity.kt
│   │   │       └── SetEntity.kt
│   │   └── datastore/
│   │       └── UserPreferences.kt
│   ├── repository/
│   │   ├── ExerciseRepositoryImpl.kt
│   │   ├── WorkoutRepositoryImpl.kt
│   │   └── ProgressionRepositoryImpl.kt
│   └── mapper/
│       └── EntityMappers.kt
│
├── domain/
│   ├── model/
│   │   ├── Exercise.kt
│   │   ├── Workout.kt
│   │   ├── ExerciseSet.kt
│   │   └── ProgressionAdvice.kt
│   ├── repository/
│   │   ├── ExerciseRepository.kt
│   │   ├── WorkoutRepository.kt
│   │   └── ProgressionRepository.kt
│   └── usecase/
│       ├── exercise/
│       │   ├── GetExercisesUseCase.kt
│       │   ├── CreateExerciseUseCase.kt
│       │   └── GetExerciseHistoryUseCase.kt
│       ├── workout/
│       │   ├── StartWorkoutUseCase.kt
│       │   ├── AddExerciseToWorkoutUseCase.kt
│       │   ├── RecordSetUseCase.kt
│       │   ├── FinishWorkoutUseCase.kt
│       │   └── GetWorkoutHistoryUseCase.kt
│       └── progression/
│           ├── CalculateE1RMUseCase.kt
│           ├── GetProgressionAdviceUseCase.kt
│           └── GetLastPerformanceUseCase.kt
│
├── presentation/
│   ├── navigation/
│   │   └── STrackerNavHost.kt
│   ├── common/
│   │   ├── components/
│   │   │   ├── SetInputCard.kt
│   │   │   ├── ExerciseCard.kt
│   │   │   ├── ProgressIndicator.kt
│   │   │   └── NumberPicker.kt
│   │   └── UiState.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── workout/
│   │   ├── active/
│   │   │   ├── ActiveWorkoutScreen.kt
│   │   │   ├── ActiveWorkoutViewModel.kt
│   │   │   └── ActiveWorkoutState.kt
│   │   └── history/
│   │       ├── WorkoutHistoryScreen.kt
│   │       └── WorkoutHistoryViewModel.kt
│   ├── exercise/
│   │   ├── library/
│   │   │   ├── ExerciseLibraryScreen.kt
│   │   │   └── ExerciseLibraryViewModel.kt
│   │   ├── detail/
│   │   │   ├── ExerciseDetailScreen.kt
│   │   │   └── ExerciseDetailViewModel.kt
│   │   └── create/
│   │       ├── CreateExerciseScreen.kt
│   │       └── CreateExerciseViewModel.kt
│   └── stats/
│       ├── StatsScreen.kt
│       └── StatsViewModel.kt
│
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
└── STrackerApp.kt
```

### 5.2 Схема слоёв

```
┌─────────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │   Screen    │  │  ViewModel  │  │   State     │                  │
│  │  (Compose)  │◄─│  (MVVM/MVI) │──│  (UiState)  │                  │
│  └─────────────┘  └──────┬──────┘  └─────────────┘                  │
│                          │                                           │
├──────────────────────────┼───────────────────────────────────────────┤
│                          ▼                                           │
│                     DOMAIN LAYER                                     │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                       UseCases                               │    │
│  │  ┌──────────────┐ ┌──────────────┐ ┌────────────────────┐   │    │
│  │  │ StartWorkout │ │  RecordSet   │ │ GetProgressAdvice  │   │    │
│  │  └──────────────┘ └──────────────┘ └────────────────────┘   │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                             │                                        │
│  ┌──────────────────────────▼──────────────────────────────────┐    │
│  │                   Repository Interfaces                      │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                             │                                        │
├─────────────────────────────┼────────────────────────────────────────┤
│                             ▼                                        │
│                       DATA LAYER                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                  Repository Implementations                   │   │
│  └───────────────────────────┬──────────────────────────────────┘   │
│                              │                                       │
│  ┌───────────────────────────▼──────────────────────────────────┐   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐   │   │
│  │  │    Room     │    │  DataStore  │    │     Mappers     │   │   │
│  │  │    DAOs     │    │ Preferences │    │  Entity↔Domain  │   │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 5.3 UiState Pattern

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val cause: Throwable? = null) : UiState<Nothing>
}

// Пример для экрана активной тренировки (MVI)
data class ActiveWorkoutState(
    val workout: Workout? = null,
    val currentExerciseIndex: Int = 0,
    val lastPerformance: Map<Long, List<ExerciseSet>> = emptyMap(),
    val progressionAdvice: Map<Long, ProgressionAdvice> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ActiveWorkoutEvent {
    data class AddExercise(val exerciseId: Long) : ActiveWorkoutEvent
    data class RecordSet(val weight: Float, val reps: Int, val rpe: Int?) : ActiveWorkoutEvent
    data class UpdateSet(val setId: Long, val weight: Float, val reps: Int) : ActiveWorkoutEvent
    data class DeleteSet(val setId: Long) : ActiveWorkoutEvent
    data object FinishWorkout : ActiveWorkoutEvent
    data object DiscardWorkout : ActiveWorkoutEvent
}
```

---

## 6. Экраны и UI/UX

### 6.1 Карта экранов

```
                              ┌─────────────────┐
                              │    HomeScreen   │
                              │  (Главный экран)│
                              └────────┬────────┘
                                       │
           ┌───────────────────────────┼───────────────────────────┐
           │                           │                           │
           ▼                           ▼                           ▼
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│  ActiveWorkoutScreen│    │ WorkoutHistoryScreen│    │   ExerciseLibrary   │
│  (Активная сессия)  │    │   (История)         │    │   (Упражнения)      │
└──────────┬──────────┘    └──────────┬──────────┘    └──────────┬──────────┘
           │                          │                          │
           ▼                          ▼                          ▼
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│ ExercisePickerSheet │    │  WorkoutDetailScreen│    │ExerciseDetailScreen │
│ (Выбор упражнения)  │    │  (Детали тренировки)│    │  (История упражн.)  │
└─────────────────────┘    └─────────────────────┘    └──────────┬──────────┘
                                                                 │
                                                                 ▼
                                                     ┌─────────────────────┐
                                                     │CreateExerciseScreen │
                                                     │ (Новое упражнение)  │
                                                     └─────────────────────┘
```

### 6.2 Детализация экранов

#### 6.2.1 HomeScreen (Главный)

```
┌─────────────────────────────────────┐
│  STracker                      ⚙️   │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────────┐│
│  │  🏋️ НАЧАТЬ ТРЕНИРОВКУ          ││
│  │     Большая кнопка              ││
│  └─────────────────────────────────┘│
│                                     │
│  ── Последняя тренировка ──         │
│  ┌─────────────────────────────────┐│
│  │ 📅 Вчера, 18:30                 ││
│  │ Грудь + Трицепс                 ││
│  │ 6 упражнений • 45 мин           ││
│  └─────────────────────────────────┘│
│                                     │
│  ── Быстрая статистика ──           │
│  ┌────────┐ ┌────────┐ ┌────────┐  │
│  │  52    │ │  156   │ │  12    │  │
│  │трениро-│ │упражн. │ │активных│  │
│  │  вок   │ │выполн. │ │ дней   │  │
│  └────────┘ └────────┘ └────────┘  │
│                                     │
├─────────────────────────────────────┤
│  🏠 Home  📜 History  💪 Exercises  │
└─────────────────────────────────────┘
```

#### 6.2.2 ActiveWorkoutScreen (MVI)

```
┌─────────────────────────────────────┐
│  ← Тренировка           ⏱️ 32:15   │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────────┐│
│  │ Жим штанги лёжа           ▼    ││
│  │ Прошлый раз: 80кг × 8, 8, 7     ││
│  │ Рекомендация: 82.5кг × 8   📈   ││
│  ├─────────────────────────────────┤│
│  │  #   Вес    Повт   RPE    ✓    ││
│  │  1   80kg   8      7     [✓]   ││
│  │  2   80kg   8      8     [✓]   ││
│  │  3   82.5   7      9     [ ]   ││  ← Текущий
│  │ ┌─────────────────────────────┐ ││
│  │ │  + Добавить подход          │ ││
│  │ └─────────────────────────────┘ ││
│  └─────────────────────────────────┘│
│                                     │
│  ┌─────────────────────────────────┐│
│  │ Разводка гантелей               ││
│  │ 3 подхода завершено        ✓   ││
│  └─────────────────────────────────┘│
│                                     │
│  ┌─────────────────────────────────┐│
│  │ ➕ Добавить упражнение          ││
│  └─────────────────────────────────┘│
│                                     │
├─────────────────────────────────────┤
│      [ 🏁 ЗАВЕРШИТЬ ТРЕНИРОВКУ ]    │
└─────────────────────────────────────┘
```

#### 6.2.3 ExerciseLibraryScreen

```
┌─────────────────────────────────────┐
│  Упражнения                    ➕   │
├─────────────────────────────────────┤
│  🔍 Поиск...                        │
├─────────────────────────────────────┤
│  Фильтр: [Все] [Грудь] [Спина] ...  │
├─────────────────────────────────────┤
│                                     │
│  ── Базовые ──                      │
│  ┌─────────────────────────────────┐│
│  │ 🏋️ Жим штанги лёжа              ││
│  │    Грудь • Последний: 82.5кг    ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 🏋️ Присед со штангой            ││
│  │    Ноги • Последний: 100кг      ││
│  └─────────────────────────────────┘│
│                                     │
│  ── Изолирующие ──                  │
│  ┌─────────────────────────────────┐│
│  │ 💪 Сгибание на бицепс           ││
│  │    Руки • Последний: 14кг       ││
│  └─────────────────────────────────┘│
│                                     │
└─────────────────────────────────────┘
```

#### 6.2.4 ExerciseDetailScreen (История упражнения)

```
┌─────────────────────────────────────┐
│  ← Жим штанги лёжа            ✏️   │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────────┐│
│  │  📈 Прогресс e1RM               ││
│  │     95kg                        ││
│  │    ╭──────────╮                 ││
│  │   ╱            ╲                ││
│  │  ╱              ───             ││
│  │ ╱                               ││
│  │ 85kg                            ││
│  │ Янв   Фев   Мар   Апр           ││
│  └─────────────────────────────────┘│
│                                     │
│  ── Последние выполнения ──         │
│  ┌─────────────────────────────────┐│
│  │ 📅 23 янв 2026                  ││
│  │ 80×8, 80×8, 82.5×7              ││
│  │ e1RM: 96kg   Объём: 1925kg      ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 📅 20 янв 2026                  ││
│  │ 80×8, 80×7, 80×6                ││
│  │ e1RM: 93kg   Объём: 1680kg      ││
│  └─────────────────────────────────┘│
│                                     │
└─────────────────────────────────────┘
```

---

## 7. Навигация

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ActiveWorkout : Screen("workout/active")
    data object WorkoutHistory : Screen("workout/history")
    data object WorkoutDetail : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    data object ExerciseLibrary : Screen("exercises")
    data object ExerciseDetail : Screen("exercises/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "exercises/$exerciseId"
    }
    data object CreateExercise : Screen("exercises/create")
}
```

---

## 8. Стек технологий

| Категория | Технология | Версия |
|-----------|------------|--------|
| **UI** | Jetpack Compose | BOM 2024.09+ |
| **UI Components** | Material 3 | latest |
| **DI** | Hilt | 2.50+ |
| **Database** | Room | 2.6+ |
| **Async** | Kotlin Coroutines + Flow | 1.8+ |
| **Navigation** | Navigation Compose | 2.7+ |
| **Preferences** | DataStore | 1.0+ |
| **Date/Time** | kotlinx-datetime | 0.5+ |

---

## 9. Предустановленные упражнения

Приложение должно содержать базовый набор упражнений при первом запуске:

### Грудь
- Жим штанги лёжа (COMPOUND)
- Жим гантелей лёжа (COMPOUND)
- Жим на наклонной скамье (COMPOUND)
- Разводка гантелей (ISOLATION)
- Сведение в кроссовере (ISOLATION)

### Спина
- Становая тяга (COMPOUND)
- Подтягивания (COMPOUND)
- Тяга штанги в наклоне (COMPOUND)
- Тяга гантели одной рукой (ACCESSORY)
- Тяга верхнего блока (ACCESSORY)

### Ноги
- Приседания со штангой (COMPOUND)
- Жим ногами (COMPOUND)
- Румынская тяга (COMPOUND)
- Выпады (ACCESSORY)
- Сгибание ног (ISOLATION)
- Разгибание ног (ISOLATION)

### Плечи
- Жим штанги стоя (COMPOUND)
- Жим гантелей сидя (COMPOUND)
- Махи гантелей в стороны (ISOLATION)
- Тяга к подбородку (ACCESSORY)

### Руки
- Сгибание штанги на бицепс (ISOLATION)
- Молотки (ISOLATION)
- Французский жим (ISOLATION)
- Разгибание на трицепс (ISOLATION)

---

## 10. Настройки приложения

| Настройка | Тип | По умолчанию |
|-----------|-----|--------------|
| Формула e1RM | Enum (Epley/Brzycki) | Epley |
| Единицы веса | Enum (kg/lbs) | kg |
| Шаг веса по умолчанию | Float | 2.5 |
| Показывать RPE | Boolean | true |
| Тема | Enum (Light/Dark/System) | System |

---

## 11. Порядок реализации (приоритет)

### Фаза 1 — MVP
1. ✅ Структура проекта + DI (Hilt)
2. ✅ Room database со всеми Entity и DAO
3. ✅ Domain-модели и маппинг
4. ✅ Репозитории
5. ✅ HomeScreen с кнопкой старта
6. ✅ ActiveWorkoutScreen — создание тренировки
7. ✅ Выбор упражнения из списка
8. ✅ Запись подходов (вес + повторения)
9. ✅ Сохранение тренировки

### Фаза 2 — История
10. WorkoutHistoryScreen — список тренировок
11. WorkoutDetailScreen — просмотр завершённой тренировки
12. Показ предыдущих результатов в ActiveWorkout

### Фаза 3 — Прогрессия
13. ExerciseDetailScreen с графиком e1RM
14. Алгоритм расчёта рекомендаций
15. Отображение рекомендаций в ActiveWorkout

### Фаза 4 — Полировка
16. Создание своих упражнений
17. Настройки приложения
18. Таймер отдыха (опционально)
19. Анимации и UX-улучшения

---

## 12. Дополнительные идеи (backlog)

- [ ] Таймер отдыха между подходами с вибрацией
- [ ] Экспорт данных в JSON/CSV
- [ ] Шаблоны тренировок (сохранять список упражнений)
- [ ] Виджет на рабочий стол с кнопкой быстрого старта
- [ ] Интеграция с Google Fit / Health Connect
- [ ] Сравнение недель/месяцев по объёму нагрузки
- [ ] Тёмная тема с OLED-оптимизацией

---

*Документ создан: 25 января 2026*
