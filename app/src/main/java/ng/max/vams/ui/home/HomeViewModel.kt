package ng.max.vams.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ng.max.vams.data.wrapper.Result
import ng.max.vams.usecase.assetreason.DownloadAssetReasonUseCaseImpl
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val downloadAssetReasonUseCaseImpl:
                                        DownloadAssetReasonUseCaseImpl) :
    ViewModel() {

    fun actionGetAssetReasons() {
        viewModelScope.launch {
            downloadAssetReasonUseCaseImpl.invoke() //silently download
        }
    }
}