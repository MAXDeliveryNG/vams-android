package ng.max.vams.ui.asset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.response.MovementStat
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(private val vehicleService: VehicleService): ViewModel() {

    private val movementStatResponse = MutableLiveData<Result<MovementStat>>()

    val getMovementStatResponse: LiveData<Result<MovementStat>> = movementStatResponse

    fun actionGetMovementStat() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getMovementStat()
                if (response.isSuccessful){
                    movementStatResponse.value = Result.Success(response.body()?.getData()!!)
                }
            }catch (ex: Exception){

            }
        }
    }
}