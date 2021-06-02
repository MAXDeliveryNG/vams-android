package ng.max.vams.ui.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.ApiEmpty
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val remoteDataSource: RemoteDataSource): ViewModel() {

    private val responseLiveData: MutableLiveData<Result<ApiEmpty>> = MutableLiveData()

    val getChangePasswordResponse: LiveData<Result<ApiEmpty>> = responseLiveData

    fun actionChangePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        UserManager.getUser()?.let { user ->
            responseLiveData.value = Result.Loading
            viewModelScope.launch {
                responseLiveData.value =
                    remoteDataSource.changePasswordRequest(user.email, currentPassword,
                        newPassword, confirmPassword)
            }
        }

    }
}