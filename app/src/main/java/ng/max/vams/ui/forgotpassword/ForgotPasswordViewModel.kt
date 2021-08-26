package ng.max.vams.ui.forgotpassword

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
class ForgotPasswordViewModel @Inject constructor(private val remoteDataSource: RemoteDataSource): ViewModel() {

    private val responseLiveData: MutableLiveData<Result<ApiEmpty>> = MutableLiveData()

    val getForgotPasswordResponse: LiveData<Result<ApiEmpty>> = responseLiveData

    fun actionForgotPassword(userEmail: String) {
        responseLiveData.value = Result.Loading
        viewModelScope.launch {
            responseLiveData.value = remoteDataSource.forgotPasswordRequest(userEmail)
        }
    }
}