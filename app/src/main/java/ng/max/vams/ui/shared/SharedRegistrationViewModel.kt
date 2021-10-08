package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ng.max.vams.data.CaptureMovementData

class SharedRegistrationViewModel: ViewModel() {

    private val movementDataResponse = MutableLiveData<CaptureMovementData>()

    val getCaptureMovementDataResponse: LiveData<CaptureMovementData> = movementDataResponse


    fun submitData(captureMovementData: CaptureMovementData) {
        movementDataResponse.value = captureMovementData
    }
}