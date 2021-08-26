package ng.max.vams.ui.registervehilce

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementData
import ng.max.vams.data.MovementReasonRepository
import ng.max.vams.data.VehicleTypeRepository
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.remote.response.VehicleType
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.search.SearchUseCase
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import javax.inject.Inject

@HiltViewModel
class RegisterVehicleViewModel @Inject constructor(
    private val movementReasonRepo: MovementReasonRepository,
    private val searchUseCase: SearchUseCase,
    private val locationRepo: LocationRepository,
    private val vehicleTypeRepo: VehicleTypeRepository,
    private val registerVehicleMovementUseCase: RegisterVehicleMovementUseCase
) : ViewModel() {

    private val reasonResponse = MutableLiveData<Result<Reason>>()
    private val locationsResponse = MutableLiveData<Result<List<Location>>>()
    private val vehicleTypeResponse = MutableLiveData<Result<VehicleType>>()
    private val vehicleTypesResponse = MutableLiveData<Result<List<VehicleType>>>()
    private val searchResponse = MutableLiveData<Result<List<Vehicle>>>()
    private val registerMovementResponse = MutableLiveData<Result<Vehicle>>()

    val getReasonResponse: LiveData<Result<Reason>> = reasonResponse
    val getLocationsResponse: LiveData<Result<List<Location>>> = locationsResponse
    val getVehicleTypeResponse: LiveData<Result<VehicleType>> = vehicleTypeResponse
    val getVehiclesTypeResponse: LiveData<Result<List<VehicleType>>> = vehicleTypesResponse
    val getSearchResponse: LiveData<Result<List<Vehicle>>> = searchResponse
    val getRegisterMovementResponse: LiveData<Result<Vehicle>> = registerMovementResponse

    fun actionGetReason(reasonId: Int) {
        reasonResponse.value = Result.Loading
        viewModelScope.launch {
            reasonResponse.value = movementReasonRepo.getMovementReasonById(reasonId)

        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun actionSearch(term: StateFlow<String>) {
        searchResponse.value = Result.Loading
        viewModelScope.launch {
            term.debounce(300)
                .filter { query ->
                    return@filter query.isNotBlank()
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    searchUseCase.invoke(query)

                }.collect { result ->
                    searchResponse.value = result
                }
        }
    }

    fun actionGetVehicleType(vehicleTypeId: Int) {
        viewModelScope.launch {
            vehicleTypeResponse.value = vehicleTypeRepo.getVehicleTypeById(vehicleTypeId)

        }

    }

    fun actionGetAllLocation() {
        locationsResponse.value = Result.Loading
        viewModelScope.launch {
            locationRepo.getLocations().collect {
                locationsResponse.value = it
            }
        }
    }

    fun actionGetAllVehicleType() {
        vehicleTypesResponse.value = Result.Loading
        viewModelScope.launch {
            vehicleTypeRepo.getVehicleTypes().collect {
                vehicleTypesResponse.value = it
            }
        }
    }

    fun registerMovement(movementData: MovementData, vehicleId: String) {
        registerMovementResponse.value = Result.Loading
        viewModelScope.launch {
            val movementBody = movementData.toMovementBody()

            movementBody.locationId = movementData.location?.let { name ->
                locationRepo.getLocationByName(name)
            }?.id
            movementBody.vehicleId = vehicleId

            registerMovementResponse.value = registerVehicleMovementUseCase.invoke(movementBody)

        }
    }
}