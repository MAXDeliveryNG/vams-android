package ng.max.vams.ui.registervehilce

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementData
import ng.max.vams.data.RetrivalChecklistRepository
import ng.max.vams.data.VehicleRepository
import ng.max.vams.data.remote.response.*
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import javax.inject.Inject

@HiltViewModel
class RegisterVehicleViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val vehicleRepo: VehicleRepository,
    private val retrivalChecklistRepo: RetrivalChecklistRepository,
    private val registerVehicleMovementUseCase: RegisterVehicleMovementUseCase,
) : ViewModel() {
    private val locationResponse = MutableLiveData<Result<Location>>()
    private val locationsResponse = MutableLiveData<Result<List<Location>>>()
    private val retrivalChecklistResponse = MutableLiveData<Result<List<RetrivalChecklistItem>>>()
    private val registerMovementResponse = MutableLiveData<Result<Vehicle>>()

    val getLocationResponse: LiveData<Result<Location>> = locationResponse
    val getLocationsResponse: LiveData<Result<List<Location>>> = locationsResponse
    val getRetrivalChecklistItemResponse: LiveData<Result<List<RetrivalChecklistItem>>> =
        retrivalChecklistResponse
    val getRegisterMovementResponse: LiveData<Result<Vehicle>> = registerMovementResponse


//    fun actionGetLocationById(locationId: String) {
//        locationResponse.value = Result.Loading
//        viewModelScope.launch {
//            locationRepo.getLocationById(locationId).collect {
//                locationResponse.value = it
//            }
//        }
//    }

    fun actionGetAllLocation() {
        locationsResponse.value = Result.Loading
        viewModelScope.launch {
            locationRepo.getLocations().collect {
                locationsResponse.value = it
            }
        }
    }

    fun actionGetRetrivalChecklist() {
        retrivalChecklistResponse.value = Result.Loading
        viewModelScope.launch {
            retrivalChecklistRepo.getRetrivalChecklistItems().collect {
                retrivalChecklistResponse.value = it
            }
        }

    }

    fun registerMovement(
        movementData: MovementData, vehicleId: String, subReasonId: String, locationToId: String?
    ) {
        registerMovementResponse.value = Result.Loading
        viewModelScope.launch {
            val movementBody = movementData.toMovementBody()

            movementBody.locationFromId = movementData.location?.let { name ->
                locationRepo.getLocationByName(name)
            }?.id
            movementBody.vehicleId = vehicleId
            movementBody.subReasonId = subReasonId
            movementBody.locationToId = locationToId

            registerMovementResponse.value = registerVehicleMovementUseCase.invoke(movementBody)

        }
    }

    fun deleteVehicle(id: String) {
        viewModelScope.launch(
            Dispatchers.IO
        ) {
            vehicleRepo.deleteVehicle(id)
        }
    }
}