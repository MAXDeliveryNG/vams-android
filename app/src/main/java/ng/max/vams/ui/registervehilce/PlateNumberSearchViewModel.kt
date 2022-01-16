package ng.max.vams.ui.registervehilce

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.remote.response.RemoteVehicle
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.search.SearchUseCase
import ng.max.vams.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PlateNumberSearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val searchResponse = SingleLiveEvent<Result<List<RemoteVehicle>>>()

    val getSearchResponse: LiveData<Result<List<RemoteVehicle>>> = searchResponse


    fun actionSearch(query: String) {
        searchResponse.value = Result.Loading
        viewModelScope.launch {
            searchResponse.value = searchUseCase.invoke(query)
        }
    }

}