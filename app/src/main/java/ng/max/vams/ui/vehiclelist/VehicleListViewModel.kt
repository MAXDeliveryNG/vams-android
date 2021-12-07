package ng.max.vams.ui.vehiclelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ng.max.vams.data.DataMapper
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.vehiclelist.VehicleListUseCase
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val vehicleListUseCase: VehicleListUseCase,
    private val remoteDataSource: RemoteDataSource
) : ViewModel() {

    private val vehicleListResponse = MutableLiveData<Result<List<DbVehicle>>>()
    private val searchResponse = MutableLiveData<Result<List<DbVehicle>>>()

    val getVehiclesResponse: LiveData<Result<List<DbVehicle>>> = vehicleListResponse
    val getSearchResponse: LiveData<Result<List<DbVehicle>>> = searchResponse

    fun actionGetVehicles(movementType: String) {
        vehicleListResponse.value = Result.Loading
        viewModelScope.launch {
            vehicleListUseCase.invoke(movementType).collect {
                vehicleListResponse.value = it
            }
        }
    }

//    @FlowPreview
//    @ExperimentalCoroutinesApi
//    fun search(term: StateFlow<String>, movementType: String) {
//        searchResponse.value = Result.Loading
//        viewModelScope.launch {
//            term.debounce(300)
//                .filter { query ->
//                    return@filter query.isNotBlank()
//                }
//                .distinctUntilChanged()
//                .flatMapLatest { query ->
//                    remoteDataSource.getSearchVehicleWithReasonResult(query, movementType)
//
//                }.collect { result ->
//                    when (result) {
//                        is Result.Error -> {
//                            searchResponse.value = result
//                        }
//                        is Result.Success -> {
//                            searchResponse.value = Result.Success(result.value.map {
//                                DataMapper().invoke(it)
//                            })
//                        }
//                        Result.Loading -> {
//                        }
//                    }
//                }
//
//        }
//    }
}