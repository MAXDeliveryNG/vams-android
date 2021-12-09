package ng.max.vams.ui.registervehilce

import android.util.Log
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

    private val reasonByNameResponse = MutableLiveData<Result<List<Reason>>>()
    val getReasonByNameResponse: LiveData<Result<List<Reason>>> = reasonByNameResponse

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

    fun actionGetReasonByName(reasonName: String){
        Log.d("TAGREASONVM", "actionGetReasonByName: $reasonName")
        reasonResponse.value = Result.Loading
        viewModelScope.launch {
            reasonByNameResponse.value = movementReasonRepo.getMovementReasonByName(reasonName)
        }
    }
}