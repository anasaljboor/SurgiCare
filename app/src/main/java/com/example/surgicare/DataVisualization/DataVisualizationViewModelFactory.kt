import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.surgicare.DataVisualization.DataVisualizationViewModel
import com.example.surgicare.SignIn.GoogleAuthClient

class DataVisualizationViewModelFactory(
    private val googleAuthClient: GoogleAuthClient,
    private val userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataVisualizationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataVisualizationViewModel(googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
