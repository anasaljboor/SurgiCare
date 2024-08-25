package com.example.surgicare.vitals

data class Vitals(
    val userId: String = "",
    val heartRate: Int = 0,       // Heart rate in beats per minute
    val bloodPressure: String = "", // Blood pressure in "systolic/diastolic" format
    val weight: Float = 0f,        // Weight in kilograms
    val bloodGroup: String = "",   // Blood group (A, B, AB, O with +/-)
    val timestamp: Long = 0L       // Timestamp for when the vitals were recorded
)

