import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.surgicare.vitals.Vitals
import com.google.firebase.firestore.FirebaseFirestore

class VitalsViewModel : ViewModel() {
    var heartRate by mutableStateOf("Loading...")
    var bloodGroup by mutableStateOf("Loading...")
    var weight by mutableStateOf("Loading...")

    private val firestore = FirebaseFirestore.getInstance()

    init {
        observeVitalsData()
    }

    private fun observeVitalsData() {
        val userId = "your-user-id"  // Replace with actual user ID

        firestore.collection("vitals").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle the error (e.g., log it)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val vitals = snapshot.toObject(Vitals::class.java)
                    heartRate = vitals?.heartRate?.toString() ?: "N/A"
                    bloodGroup = vitals?.bloodGroup ?: "N/A"
                    weight = vitals?.weight?.toString() ?: "N/A"
                } else {
                    // Handle case where the document doesn't exist
                    heartRate = "Not Available"
                    bloodGroup = "Not Available"
                    weight = "Not Available"
                }
            }
    }
}
