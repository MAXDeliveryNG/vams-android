package ng.max.vams.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementReasonRepository
import ng.max.vams.data.VehicleRepository
import ng.max.vams.data.VehicleTypeRepository
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.MovementStat
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val remoteDataSource:
                                        RemoteDataSource,
                                        private val vehicleRepo: VehicleRepository,
                                        private val locationRepo: LocationRepository,
                                        private val vehicleTypeRepo: VehicleTypeRepository,
                                        private val movementReasonRepo: MovementReasonRepository
) :
    ViewModel() {

    private val movementStatResponse = MutableLiveData<Result<MovementStat>>()

    val getMovementStatResponse: LiveData<Result<MovementStat>> = movementStatResponse

    fun actionGetMovementStat() {
        viewModelScope.launch {
            movementStatResponse.value = remoteDataSource.getMovementStat()
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

    fun clearVehicleTable() {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepo.deleteVehicleData()
        }
    }
}