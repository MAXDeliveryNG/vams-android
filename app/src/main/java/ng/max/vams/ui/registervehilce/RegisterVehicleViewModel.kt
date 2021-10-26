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
import ng.max.vams.data.VehicleRepository
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import javax.inject.Inject

@HiltViewModel
class RegisterVehicleViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val vehicleRepo: VehicleRepository,
    private val registerVehicleMovementUseCase: RegisterVehicleMovementUseCase
) : ViewModel() {
    private val locationsResponse = MutableLiveData<Result<List<Location>>>()
    private val registerMovementResponse = MutableLiveData<Result<Vehicle>>()

    val getLocationsResponse: LiveData<Result<List<Location>>> = locationsResponse
    val getRegisterMovementResponse: LiveData<Result<Vehicle>> = registerMovementResponse


    fun actionGetAllLocation() {
        locationsResponse.value = Result.Loading
        viewModelScope.launch {
            locationRepo.getLocations().collect {
                locationsResponse.value = it
            }
        }
    }


    fun registerMovement(movementData: MovementData, vehicleId: String, subReasonId: String, locationToId: Int,
                         movementType: String) {
        registerMovementResponse.value = Result.Loading
        viewModelScope.launch {
            val movementBody = movementData.toMovementBody()

            movementBody.locationFromId = movementData.location?.let { name ->
                locationRepo.getLocationByName(name)
            }?.id
            movementBody.vehicleId = vehicleId
            movementBody.subReasonId = subReasonId
            if (locationToId != 0){
                movementBody.locationToId = locationToId
            }
            movementBody.movementType = movementType

            registerMovementResponse.value = registerVehicleMovementUseCase.invoke(movementBody)

        }
    }

    fun deleteVehicle(id: String) {
        viewModelScope.launch(Dispatchers.IO
        ) {
            vehicleRepo.deleteVehicle(id)
        }
    }
}