package com.example.surgicare.notifications

import android.content.Context
import androidx.work.*
import com.example.surgicare.SignIn.GoogleAuthClient
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context, private val googleAuthClient: GoogleAuthClient) {

    private val firestore = FirebaseFirestore.getInstance()

    // Schedule a one-time notification using WorkManager
    fun scheduleNotification(
        title: String,
        message: String,
        delayInMillis: Long
    ) {
        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "title" to title,
                    "message" to message
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(notificationWork)

        // Save notification to Firestore
        saveNotificationToFirestore(title, message)
    }

    // Schedule a notification at a specific time
    fun scheduleNotificationAtTime(
        title: String,
        message: String,
        scheduleTimeInMillis: Long
    ) {
        val currentTimeInMillis = System.currentTimeMillis()
        val delayInMillis = scheduleTimeInMillis - currentTimeInMillis

        if (delayInMillis > 0) {
            scheduleNotification(title, message, delayInMillis)
        } else {
            // Time has already passed; handle accordingly
        }
    }

    // Schedule a repeating notification
    fun scheduleRepeatingNotification(
        title: String,
        message: String,
        repeatIntervalMillis: Long,
        initialDelayMillis: Long = 0L,
        uniqueWorkName: String
    ) {
        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatIntervalMillis, TimeUnit.MILLISECONDS
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "title" to title,
                    "message" to message
                )
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )

        // Save repeating notification to Firestore
        saveNotificationToFirestore(title, message)
    }

    // Get the current user's ID using GoogleAuthClient
    private fun getCurrentUserId(): String? {
        return googleAuthClient.getSignedInUser()?.userId
    }

    // Save the notification data to Firestore
    private fun saveNotificationToFirestore(title: String, message: String) {
        val userId = getCurrentUserId()
        if (userId != null) {
            val notificationData = hashMapOf(
                "title" to title,
                "message" to message,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            firestore.collection("patients").document(userId)
                .collection("notifications")
                .add(notificationData)
                .addOnSuccessListener {
                    println("Notification saved to Firestore")
                }
                .addOnFailureListener { e ->
                    println("Error saving notification: ${e.message}")
                }
        } else {
            println("User ID is null, notification not saved")
        }
    }

    // Send Vitals Update Notification
    fun sendVitalsNotification(title: String, message: String) {
        scheduleNotification(title, message, 0)
    }

    // Send Medication Reminder
    fun scheduleMedicationReminder(isMorning: Boolean) {
        val timeOfDay = if (isMorning) "Morning" else "Evening"
        val hourOfDay = if (isMorning) 8 else 20 // 8 AM or 8 PM
        val uniqueWorkName = if (isMorning) "MEDICATION_REMINDER_MORNING" else "MEDICATION_REMINDER_EVENING"

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        scheduleRepeatingNotification(
            title = "$timeOfDay Medication Reminder",
            message = "It's time to take your medication.",
            repeatIntervalMillis = TimeUnit.DAYS.toMillis(1),
            initialDelayMillis = initialDelay,
            uniqueWorkName = uniqueWorkName
        )
    }

    // Send Appointment Reminder Notification
    fun scheduleAppointmentReminder(appointmentTimeInMillis: Long, appointmentTimeString: String) {
        val title = "Appointment Reminder"
        val message = "Your next appointment is at $appointmentTimeString."

        scheduleNotificationAtTime(title, message, appointmentTimeInMillis)
    }

    // Send Daily Health Check-In Reminder
    fun scheduleDailyHealthCheckIn() {
        val hourOfDay = 9 // 9 AM
        val uniqueWorkName = "DAILY_HEALTH_CHECKIN"

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        scheduleRepeatingNotification(
            title = "Daily Health Check-In",
            message = "Don't forget to complete your health check-in today.",
            repeatIntervalMillis = TimeUnit.DAYS.toMillis(1),
            initialDelayMillis = initialDelay,
            uniqueWorkName = uniqueWorkName
        )
    }

    // Send Critical Alert
    fun sendCriticalAlert(title: String, message: String) {
        scheduleNotification(
            title = title,
            message = message,
            delayInMillis = 0 // Send immediately
        )
    }
}
