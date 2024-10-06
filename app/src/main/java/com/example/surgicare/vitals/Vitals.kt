package com.example.surgicare.vitals

import com.google.firebase.Timestamp

data class Vitals(
    val heartRate: Int? = null,
    val bloodPressure: String? = null,
    val weight: Float? = null,
    val bloodGroup: String? = null,
    val spO2: Int? = null,
    val temperature: Float? = null,
    val respiratoryRate: Int? = null,
    val timestamp: Timestamp? = null // Use com.google.firebase.Timestamp
)
