package com.example.surgicare.DataVisualization

import kotlin.collections.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surgicare.SignIn.GoogleAuthClient
import com.example.surgicare.vitals.Vitals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class DataVisualizationViewModel(private val googleAuthClient: GoogleAuthClient) : ViewModel() {

    // State flows to hold vitals history and averages
    private val _vitalsHistory = MutableStateFlow<List<Vitals>>(emptyList())
    val vitalsHistory: StateFlow<List<Vitals>> = _vitalsHistory

    private val _vitalsAverages = MutableStateFlow<Map<String, Float>>(emptyMap())
    val vitalsAverages: StateFlow<Map<String, Float>> = _vitalsAverages

    // Function to fetch vitals data from Firestore
    fun fetchVitals(userId: String) {
        viewModelScope.launch {
            val oneWeekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)

            try {
                // Fetch vitals from the past 7 days
                val snapshot = googleAuthClient.firestore.collection("patients")
                    .document(userId)
                    .collection("vitals_history")
                    .whereGreaterThan("timestamp", oneWeekAgo)
                    .get()
                    .await()

                // Convert snapshot to list of vitals
                val vitalsList = snapshot.toObjects(Vitals::class.java)
                _vitalsHistory.value = vitalsList

                // Calculate averages based on fetched data
                calculateAverages(vitalsList)
            } catch (e: Exception) {
                println("Error fetching vitals: ${e.message}")
            }
        }
    }

    // Function to calculate averages of vitals over the past 7 days
    private fun calculateAverages(vitalsList: List<Vitals>) {
        val avgHeartRate = vitalsList.mapNotNull { it.heartRate }.average().toFloat()
        val avgSpO2 = vitalsList.mapNotNull { it.spO2 }.average().toFloat()
        val avgTemperature = vitalsList.mapNotNull { it.temperature }.average().toFloat()
        val avgRespiratoryRate = vitalsList.mapNotNull { it.respiratoryRate }.average().toFloat()

        _vitalsAverages.value = mapOf(
            "Heart Rate" to avgHeartRate,
            "SpO2" to avgSpO2,
            "Temperature" to avgTemperature,
            "Respiratory Rate" to avgRespiratoryRate
        )
    }
}
