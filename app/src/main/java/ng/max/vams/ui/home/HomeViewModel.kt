package ng.max.vams.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.RemoteDataSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val remoteDataSource:
                                        RemoteDataSource) :
    ViewModel() {

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