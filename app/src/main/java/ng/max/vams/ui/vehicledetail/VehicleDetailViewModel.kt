package ng.max.vams.ui.vehicledetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.dao.VehicleDao
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(private val vehicleDao: VehicleDao): ViewModel() {

    private val vehicleResponse = MutableLiveData<Result<DbVehicle>>()

    val getVehiclesResponse: LiveData<Result<DbVehicle>> = vehicleResponse

    fun actionGetVehicleDetail(vehicleId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val vehicle = vehicleDao.getVehicleById(vehicleId)
                    vehicleResponse.postValue(Result.Success(vehicle))
                }catch (ex: Exception){
                    vehicleResponse.postValue(Result.Error("Error getting vehicle detail"))
                }
            }

        }
    }
}