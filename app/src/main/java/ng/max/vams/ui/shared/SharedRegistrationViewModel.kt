package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.CaptureMovementData
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementData
import ng.max.vams.data.RetrievalChecklistRepository
import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.RemoteVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import ng.max.vams.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SharedRegistrationViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val retrievalChecklistRepo: RetrievalChecklistRepository,
    private val registerVehicleMovementUseCase: RegisterVehicleMovementUseCase,
) : ViewModel() {

    private val movementDataResponse = MutableLiveData<CaptureMovementData>()
    private val registerMovementResponse = SingleLiveEvent<Result<RemoteVehicle>>()

    val getCaptureMovementDataResponse: LiveData<CaptureMovementData> = movementDataResponse
    val getRegisterMovementResponse: LiveData<Result<RemoteVehicle>> = registerMovementResponse


    fun submitData(captureMovementData: CaptureMovementData) {
        movementDataResponse.value = captureMovementData
    }

    fun registerMovement(
        movementData: MovementData, vehicleId: String, subReasonId: String, locationToId: String?, changeOption: String, transferStatus: String? = null) {
        registerMovementResponse.value = Result.Loading
        viewModelScope.launch {
            val movementBody = movementData.toMovementBody()

            movementBody.locationFromId = movementData.location?.let { name ->
                locationRepo.getLocationByName(name)
            }?.id
            movementBody.vehicleId = vehicleId
            movementBody.retrievalAgent = movementData.retrievalAgent
            movementBody.vehicleMovement = movementData.vehicleMovement
            movementBody.subReasonId = subReasonId
            movementBody.locationToId = locationToId
            movementBody.transferStatus = transferStatus

            registerMovementResponse.value = registerVehicleMovementUseCase.invoke(changeOption, movementBody)

        }
    }

    fun registerMovementFromReasonScreen(
        movementBody: MovementBody,
        changeOption: String
    ) {
        registerMovementResponse.value = Result.Loading
        viewModelScope.launch {
            movementBody.apply {
                recoveredItems = getRecoveredItemsIds(movementBody.recoveredItems)
            }
            registerMovementResponse.value = registerVehicleMovementUseCase.invoke(changeOption, movementBody)

        }
    }

    private suspend fun getRecoveredItemsIds(checkListItems: List<String>?): List<String> {
        val dbData = retrievalChecklistRepo.getCheckListItems()
        val retrievedItemsIds = mutableListOf<String>()
        checkListItems?.forEach {retrievedItemName ->
            val filteredData = dbData.filter {
                it.name == retrievedItemName
            }
            if (filteredData.isNotEmpty()){
                retrievedItemsIds.add(filteredData.first().id)
            }
        }
        return retrievedItemsIds
    }
}