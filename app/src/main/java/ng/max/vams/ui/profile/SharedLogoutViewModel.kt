package ng.max.vams.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ng.max.vams.util.SingleLiveEvent

class SharedLogoutViewModel: ViewModel() {
    private val shouldLogoutResponse = SingleLiveEvent<Boolean>()

    val getShouldLogoutResponse: LiveData<Boolean> = shouldLogoutResponse

    fun actionLogout(shouldLogout: Boolean){
        shouldLogoutResponse.value = shouldLogout
    }
}