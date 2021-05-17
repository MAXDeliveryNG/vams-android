package ng.max.vams.ui.vehiclelist

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.vehiclelist.VehicleListUseCase
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(private val vehicleListUseCase: VehicleListUseCase): ViewModel() {

    private val vehicleListResponse = MutableLiveData<Result<List<DbVehicle>>>()

    val getVehiclesResponse: LiveData<Result<List<DbVehicle>>> = vehicleListResponse

    fun actionGetVehicles(movementType:String) {
        vehicleListResponse.value = Result.Loading
        viewModelScope.launch {
            vehicleListUseCase.invoke(movementType).collect {
                vehicleListResponse.value = it
            }
        }
    }
}