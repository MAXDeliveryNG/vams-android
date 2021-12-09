package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.util.SingleLiveEvent

class SharedBottomSheetViewModel : ViewModel() {

    private val selectedItemResponse = SingleLiveEvent<Map<String, String>>()
    private val selectedTransferLocationResponse = SingleLiveEvent<String>()
    private val selectedTransferLocationIdResponse = SingleLiveEvent<String>()
    private val subReasonsResponse = SingleLiveEvent<List<SubReason>>()
    private val locationsResponse = SingleLiveEvent<List<Location>>()

    val getSelectedItemResponse: LiveData<Map<String, String>> = selectedItemResponse
    val getSubReasonsResponse: LiveData<List<SubReason>> = subReasonsResponse
    val getLocationsResponse: LiveData<List<Location>> = locationsResponse
    val getSelectedTransferLocationIdResponse: LiveData<String> = selectedTransferLocationIdResponse
    val getSelectedTransferLocationResponse: LiveData<String> = selectedTransferLocationResponse


    fun submitSelectedItem(selectedItem: Map<String, String>) {
        selectedItemResponse.value  = selectedItem
    }


    fun submitSelectedTransferLocationId(selectedItem: String) {
        selectedTransferLocationIdResponse.value  = selectedItem
    }

    fun submitSelectedItemForTransferLocation(selectedItem: String) {
        selectedTransferLocationResponse.value  = selectedItem
    }

    fun submitSubReasons(subReasons: List<SubReason>) {
        subReasonsResponse.value = subReasons
    }

    fun submitLocations(locations: List<Location>) {
        locationsResponse.value = locations
    }
}