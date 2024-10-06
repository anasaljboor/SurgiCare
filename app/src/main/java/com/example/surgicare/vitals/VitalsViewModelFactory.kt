import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.surgicare.SignIn.GoogleAuthClient
import com.example.surgicare.vitals.VitalsViewModel

class VitalsViewModelFactory(
    private val application: Application,
    private val googleAuthClient: GoogleAuthClient  // Add GoogleAuthClient as a parameter
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VitalsViewModel::class.java)) {
            return VitalsViewModel(application, googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
