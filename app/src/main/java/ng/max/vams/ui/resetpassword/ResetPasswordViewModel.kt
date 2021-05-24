package ng.max.vams.ui.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.ApiEmpty
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val remoteDataSource: RemoteDataSource): ViewModel() {

    private val responseLiveData: MutableLiveData<Result<ApiEmpty>> = MutableLiveData()
    private val validateResponse = MutableLiveData<Result<Boolean>>()

    val getResetPasswordResponse: LiveData<Result<ApiEmpty>> = responseLiveData
    val getValidateResponse: LiveData<Result<Boolean>> = validateResponse

    fun actionResetPassword(userEmail: String, tempPassword: String, newPassword: String) {
        responseLiveData.value = Result.Loading
        viewModelScope.launch {
            responseLiveData.value = remoteDataSource.resetPasswordRequest(userEmail, tempPassword, newPassword)
        }
    }

    fun actionValidate(email: String, tempPassword: String, password: String) {
        when {
            email.isEmpty() -> {
                validateResponse.value = Result.Error("Please enter your email")
            }
            tempPassword.isEmpty() -> {
                validateResponse.value = Result.Error("Please enter your temp password")
            }
            password.isEmpty() -> {
                validateResponse.value = Result.Error("Please enter your password")
            }
            else -> {
                validateResponse.value = Result.Success(true)
            }
        }
    }
}