package ng.max.vams.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.MovementStat
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val remoteDataSource:
                                        RemoteDataSource) :
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
            remoteDataSource.getMovementReasons() //silently download
        }
    }

    fun actionGetLocations() {
        viewModelScope.launch {
            remoteDataSource.getLocations() //silently download
        }
    }

    fun actionGetVehicleTypes() {
        viewModelScope.launch {
            remoteDataSource.getVehicleType() //silently download
        }
    }
}