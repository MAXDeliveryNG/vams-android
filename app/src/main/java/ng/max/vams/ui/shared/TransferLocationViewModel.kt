package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class TransferLocationViewModel @Inject constructor(
    private val locationRepo: LocationRepository
) : ViewModel() {
    private val locationsResponse = MutableLiveData<Result<List<Location>>>()

    val getLocationsResponse: LiveData<Result<List<Location>>> = locationsResponse


    fun actionGetAllLocation() {
        locationsResponse.value = Result.Loading
        viewModelScope.launch {
            locationRepo.getLocations().collect {
                locationsResponse.value = it
            }
        }
    }

}