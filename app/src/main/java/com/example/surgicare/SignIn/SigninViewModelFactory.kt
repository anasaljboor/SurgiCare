import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.surgicare.SignIn.GoogleAuthClient
import com.example.surgicare.SignIn.SignInViewModel

class SignInViewModelFactory(
    private val googleAuthClient: GoogleAuthClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}