package ng.max.vams.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.*
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.FullMovementStat
import ng.max.vams.data.remote.response.RoleData
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.userrole.UserRoleUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val remoteDataSource:
                                        RemoteDataSource,
                                        private val vehicleRepo: VehicleRepository,
                                        private val locationRepo: LocationRepository,
                                        private val vehicleTypeRepo: VehicleTypeRepository,
                                        private val movementReasonRepo: MovementReasonRepository,
                                        private val retrievalChecklistRepo: RetrievalChecklistRepository,
                                        private val userRoleUseCase: UserRoleUseCase

) :
    ViewModel() {

    private val unconfirmedVehicleResponse = MutableLiveData<Result<List<DbVehicle>>>()
    private val fullMovementStatResponse = MutableLiveData<Result<FullMovementStat>>()
    private val userRoleResponse = MutableLiveData<Result<RoleData>>()
    private val cardControlResponse = MutableLiveData<MutableMap<String,Boolean>>()

    val getUserRoleResponse: LiveData<Result<RoleData>> = userRoleResponse
    val getcardControlResponse: LiveData<MutableMap<String,Boolean>> = cardControlResponse
    val getUnconfirmedVehicleResponse: LiveData<Result<List<DbVehicle>>> = unconfirmedVehicleResponse
    val getFullMovementStatResponse: LiveData<Result<FullMovementStat>> = fullMovementStatResponse

    fun controlCard(control: MutableMap<String, Boolean>, controlKey: String, controlState: Boolean){
        val newControl = mutableMapOf<String, Boolean>()
        if(controlState){
                control.map{
                    if(it.key != controlKey){
                        newControl[it.key] = false
                    }
            }
        }
        cardControlResponse.value = newControl
    }

    fun actionGetUnconfirmedVehicles() {
        viewModelScope.launch {
            vehicleRepo.getUnConfirmedVehicles().collect {
                unconfirmedVehicleResponse.value = it
            }
        }
    }

    fun actionGetFullMovementStat(userId: String){
        viewModelScope.launch {
            fullMovementStatResponse.value = remoteDataSource.getFullMovementStat(userId)
        }
    }

    fun actionGetAssetReasons() {
        viewModelScope.launch {
            movementReasonRepo.getMovementReasons() //silently download
        }
    }

    fun actionGetLocations() {
        viewModelScope.launch {
            locationRepo.getLocations() //silently download
        }
    }

    fun actionGetVehicleTypes() {
        viewModelScope.launch {
            vehicleTypeRepo.getVehicleTypes() //silently download
        }
    }

    fun actionGetVehicleCheckListItem() {
        viewModelScope.launch {
            retrievalChecklistRepo.getRetrievalChecklistItems() //silently download
        }
    }

    fun clearVehicleTable() {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepo.deleteVehicleData()
        }
    }

    fun getUserRole(userId: String){
        viewModelScope.launch {
            userRoleResponse.value = userRoleUseCase.invokeRole(userId)
        }
    }
}