package ng.max.vams.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.manager.UserManager
import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.User
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.login.LoginUseCase
import ng.max.vams.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val loggedInUserResponse = MutableLiveData<User?>()
    private val loginResponse = SingleLiveEvent<Result<User>>()

    fun getLoggedInUser() : LiveData<User?>{
        loggedInUserResponse.value = if (UserManager.getUser() != null){
            UserManager.getUser()
        }else{
            null
        }
        return loggedInUserResponse
    }
    val getLoginResponse: LiveData<Result<User>> = loginResponse


    fun actionLogin(username: String, password: String) {
        loginResponse.value = Result.Loading
        val loginBody = LoginBody(username, password)
        viewModelScope.launch {
            when (val result = loginUseCase.invoke(loginBody)) {
                is Result.Success -> {
                    val loginData = result.value
                    UserManager.saveToken(loginData.accessToken)
                    UserManager.saveUser(loginData.user)
                    loginResponse.value = Result.Success(loginData.user)
                }
                is Result.Error -> {
                    loginResponse.value = Result.Error(result.message)
                }
                is Result.Loading -> {
                    loginResponse.value = Result.Loading
                }
            }
        }
    }
}