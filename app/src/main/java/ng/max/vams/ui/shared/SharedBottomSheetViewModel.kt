package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ng.max.vams.util.SingleLiveEvent

class SharedBottomSheetViewModel : ViewModel() {

    private val selectedItemResponse = SingleLiveEvent<String>()
    private val selectedTransferLocationResponse = SingleLiveEvent<String>()
    private val selectedTransferLocationIdResponse = SingleLiveEvent<String>()

    val getSelectedItemResponse: LiveData<String> = selectedItemResponse
    val getSelectedTransferLocationIdResponse: LiveData<String> = selectedTransferLocationIdResponse
    val getSelectedTransferLocationResponse: LiveData<String> = selectedTransferLocationResponse


    fun submitSelectedItem(selectedItem: String) {
        selectedItemResponse.value  = selectedItem
    }


    fun submitSelectedTransferLocationId(selectedItem: String) {
        selectedTransferLocationIdResponse.value  = selectedItem
    }

    fun submitSelectedItemForTransferLocation(selectedItem: String) {
        selectedTransferLocationResponse.value  = selectedItem
    }
}