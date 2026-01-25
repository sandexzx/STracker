package com.example.stracker.domain.model

enum class E1RMFormula(val displayName: String) {
    EPLEY("Epley"),
    BRZYCKI("Brzycki")
}

enum class WeightUnit(val displayName: String, val symbol: String) {
    KG("Килограммы", "кг"),
    LBS("Фунты", "lbs")
}

data class UserSettings(
    val e1rmFormula: E1RMFormula = E1RMFormula.EPLEY,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val defaultWeightStep: Float = 2.5f,
    val showRpe: Boolean = true,
    val theme: String = "system", // "light", "dark", "system"
    val restTimerEnabled: Boolean = false,
    val defaultRestSeconds: Int = 90
)
