package ng.max.vams.ui.asset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(private val vehicleService: VehicleService): ViewModel() {

    private val checkInCountResponse = MutableLiveData<Result<Int>>()
    private val checkOutCountResponse = MutableLiveData<Result<Int>>()

    val getCheckInCountResponse: LiveData<Result<Int>> = checkInCountResponse
    val getCheckOutCountResponse: LiveData<Result<Int>> = checkOutCountResponse

    fun actionGetVehicleCounts() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getVehicleListCount("checked_in")
                if (response.isSuccessful){
                    checkInCountResponse.value = Result.Success(response.body()?.vehicleListData?.vehicles?.count()!!)
                }
            }catch (ex: Exception){

            }

            try {
                val response = vehicleService.getVehicleListCount("checked_out")
                if (response.isSuccessful){
                    checkOutCountResponse.value = Result.Success(response.body()?.vehicleListData?.vehicles?.count()!!)
                }
            }catch (ex: Exception){

            }
        }
    }
}