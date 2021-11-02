package ng.max.vams.ui.registervehilce

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementReasonRepository
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

@HiltViewModel
class SelectMovementReasonViewModel @Inject constructor(
    private val movementReasonRepo: MovementReasonRepository,

    private val locationRepo: LocationRepository
) : ViewModel() {

    private val reasonResponse = MutableLiveData<Result<List<Reason>>>()
    val getReasonsResponse: LiveData<Result<List<Reason>>> = reasonResponse

    fun actionGetReasons() {
        reasonResponse.value = Result.Loading
        viewModelScope.launch {
            movementReasonRepo.getMovementReasons().collect {
                reasonResponse.value = it
            }
        }
    }

    fun actionGetLocations() {
        viewModelScope.launch {
            locationRepo.getLocations() //silently download
        }
    }
}