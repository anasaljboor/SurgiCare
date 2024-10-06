package com.example.surgicare.vitals

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.surgicare.SignIn.GoogleAuthClient
import com.example.surgicare.notifications.NotificationHelper
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VitalsViewModel(
    application: Application,
    private val googleAuthClient: GoogleAuthClient
) : AndroidViewModel(application) {

    var heartRate by mutableStateOf("Loading...")
    var bloodGroup by mutableStateOf("Loading...")
    var weight by mutableStateOf("Loading...")
    var bloodPressure by mutableStateOf("Loading...")
    var spO2 by mutableStateOf("Loading...")
    var temperature by mutableStateOf("Loading...")
    var respiratoryRate by mutableStateOf("Loading...")
    var timestamp by mutableStateOf("Loading...")

    private val notificationHelper = NotificationHelper(application, googleAuthClient)

    private val LOW_CRITICAL_HEART_RATE = 60   // Bradycardia
    private val HIGH_CRITICAL_HEART_RATE = 100 // Tachycardia

    // SpO2 (oxygen saturation in %)
    private val LOW_CRITICAL_SPO2 = 90         // Hypoxemia

    // Temperature (Celsius)
    private val LOW_CRITICAL_TEMPERATURE = 35.0f // Hypothermia
    private val HIGH_CRITICAL_TEMPERATURE = 38.0f // Fever

    // Respiratory Rate (breaths per minute)
    private val LOW_CRITICAL_RESPIRATORY_RATE = 12 // Bradypnea
    private val HIGH_CRITICAL_RESPIRATORY_RATE = 20 // Tachypnea
    init {
        observeVitalsData()
    }

    private fun observeVitalsData() {
        val userId = googleAuthClient.getSignedInUser()?.userId ?: return

        googleAuthClient.firestore.collection("patients").document(userId)
            .collection("vitals").document("latestVitals")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    println("Error fetching vitals: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val vitals = snapshot.toObject(Vitals::class.java)

                    heartRate = vitals?.heartRate?.toString() ?: "N/A"
                    bloodGroup = vitals?.bloodGroup ?: "N/A"
                    weight = vitals?.weight?.toString() ?: "N/A"
                    bloodPressure = vitals?.bloodPressure ?: "N/A"
                    spO2 = vitals?.spO2?.toString() ?: "N/A"
                    temperature = vitals?.temperature?.toString() ?: "N/A"
                    respiratoryRate = vitals?.respiratoryRate?.toString() ?: "N/A"

                    // Handle Timestamp
                    timestamp = vitals?.timestamp?.let { firebaseTimestamp ->
                        formatTimestamp(firebaseTimestamp.toDate())
                    } ?: "N/A"

                    // Check for critical levels and send notifications
                    vitals?.let { checkForCriticalVitals(it) }
                } else {
                    // Handle case when no vitals document exists yet
                    heartRate = "Not Available"
                    bloodGroup = "Not Available"
                    weight = "Not Available"
                    bloodPressure = "Not Available"
                    spO2 = "Not Available"
                    temperature = "Not Available"
                    respiratoryRate = "Not Available"
                    timestamp = "Not Available"
                }
            }
    }

    // Function to add new vitals to Firestore
    fun addVitals(
        heartRate: Int,
        bloodPressure: String,
        weight: Float,
        bloodGroup: String,
        spO2: Int,
        temperature: Float,
        respiratoryRate: Int
    ) {
        val userId = googleAuthClient.getSignedInUser()?.userId ?: return

        val vitalsData = hashMapOf(
            "heartRate" to heartRate,
            "bloodPressure" to bloodPressure,
            "weight" to weight,
            "bloodGroup" to bloodGroup,
            "spO2" to spO2,
            "temperature" to temperature,
            "respiratoryRate" to respiratoryRate,
            "timestamp" to FieldValue.serverTimestamp() // Store server timestamp
        )

        // 1. Store the latest vitals in a specific document
        googleAuthClient.firestore.collection("patients").document(userId)
            .collection("vitals")
            .document("latestVitals")  // Overwrites the document for latest vitals
            .set(vitalsData)
            .addOnSuccessListener {
                println("Latest vitals updated successfully")
            }
            .addOnFailureListener { e ->
                println("Error updating latest vitals: ${e.message}")
            }

        // 2. Store historical vitals with an auto-generated ID to preserve the history
        googleAuthClient.firestore.collection("patients").document(userId)
            .collection("vitals_history")  // A new collection to store historical data
            .add(vitalsData)  // Firestore generates a unique document ID
            .addOnSuccessListener {
                println("Historical vitals added successfully")
            }
            .addOnFailureListener { e ->
                println("Error adding historical vitals: ${e.message}")
            }
    }


    private fun formatTimestamp(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    private fun checkForCriticalVitals(vitals: Vitals) {
        // Check Heart Rate
        vitals.heartRate?.let { hr ->
            when {
                hr > HIGH_CRITICAL_HEART_RATE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "High Heart Rate Detected",
                        message = "Your heart rate is $hr bpm, which is above the normal range."
                    )
                }

                hr < LOW_CRITICAL_HEART_RATE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "Low Heart Rate Detected",
                        message = "Your heart rate is $hr bpm, which is below the normal range."
                    )
                }
            }
        }

        // Check SpO2
        vitals.spO2?.let { spo2 ->
            if (spo2 < LOW_CRITICAL_SPO2) {
                notificationHelper.sendCriticalAlert(
                    title = "Low Oxygen Saturation",
                    message = "Your SpO₂ level is $spo2%, which is below the normal range."
                )
            }
        }

        // Check Temperature
        vitals.temperature?.let { temp ->
            when {
                temp > HIGH_CRITICAL_TEMPERATURE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "High Body Temperature",
                        message = "Your body temperature is $temp°C, indicating a fever."
                    )
                }

                temp < LOW_CRITICAL_TEMPERATURE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "Low Body Temperature",
                        message = "Your body temperature is $temp°C, which is below the normal range."
                    )
                }
            }
        }

        // Check Respiratory Rate
        vitals.respiratoryRate?.let { rr ->
            when {
                rr > HIGH_CRITICAL_RESPIRATORY_RATE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "High Respiratory Rate",
                        message = "Your respiratory rate is $rr breaths per minute, which is above the normal range."
                    )
                }

                rr < LOW_CRITICAL_RESPIRATORY_RATE -> {
                    notificationHelper.sendCriticalAlert(
                        title = "Low Respiratory Rate",
                        message = "Your respiratory rate is $rr breaths per minute, which is below the normal range."
                    )
                }
            }

        }
    }
}
