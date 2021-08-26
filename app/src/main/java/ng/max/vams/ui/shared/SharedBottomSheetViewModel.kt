package ng.max.vams.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ng.max.vams.util.SingleLiveEvent

class SharedBottomSheetViewModel : ViewModel() {

    private val selectedItemResponse = SingleLiveEvent<Map<String, String>>()

    val getSelectedItemResponse: LiveData<Map<String, String>> = selectedItemResponse


    fun submitSelectedItem(selectedItem: Map<String, String>) {
        selectedItemResponse.value  = selectedItem
    }
}