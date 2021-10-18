package ng.max.vams.ui.assetreason

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ng.max.vams.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class VehicleConfirmationViewModel @Inject constructor(): ViewModel() {

    private val confirmationResponse = SingleLiveEvent<Boolean>()

    val getConfirmationResponse: LiveData<Boolean> =confirmationResponse


    fun submitConfirmation(hasConfirm: Boolean) {
        confirmationResponse.value = hasConfirm
    }
}