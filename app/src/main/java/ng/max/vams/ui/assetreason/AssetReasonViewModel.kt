package ng.max.vams.ui.assetreason

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.assetreason.GetAssetReasonUseCaseImpl
import ng.max.vams.usecase.vehiclelist.VehicleListUseCaseImpl
import javax.inject.Inject

@HiltViewModel
class AssetReasonViewModel @Inject constructor(private val getAssetReasonUseCaseImpl: GetAssetReasonUseCaseImpl): ViewModel() {

    private val reasonResponse = MutableLiveData<Result<List<Reason>>>()
    val getReasonsResponse: LiveData<Result<List<Reason>>> = reasonResponse

    fun actionGetReasons(movementType: String) {
        reasonResponse.value = Result.Loading
        viewModelScope.launch {
            getAssetReasonUseCaseImpl.invoke(movementType).collect {
                reasonResponse.value = it
            }
        }
    }
}