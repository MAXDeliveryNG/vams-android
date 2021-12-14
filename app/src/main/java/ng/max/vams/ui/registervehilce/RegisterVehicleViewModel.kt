package ng.max.vams.ui.registervehilce

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.RetrievalChecklistRepository
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class RegisterVehicleViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val retrievalChecklistRepo: RetrievalChecklistRepository,
) : ViewModel() {

    private val locationsResponse = MutableLiveData<Result<List<Location>>>()
    private val retrievalChecklistResponse = MutableLiveData<Result<List<RetrivalChecklistItem>>>()

    val getLocationsResponse: LiveData<Result<List<Location>>> = locationsResponse
    val getRetrievalChecklistItemResponse: LiveData<Result<List<RetrivalChecklistItem>>> =
        retrievalChecklistResponse


    fun actionGetAllLocation() {
        locationsResponse.value = Result.Loading
        viewModelScope.launch {
            locationRepo.getLocations().collect {
                locationsResponse.value = it
            }
        }
    }

    fun actionGetRetrievalChecklist() {
        retrievalChecklistResponse.value = Result.Loading
        viewModelScope.launch {
            retrievalChecklistRepo.getRetrievalChecklistItems().collect {
                retrievalChecklistResponse.value = it
            }
        }

    }

}