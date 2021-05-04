package ng.max.vams.usecase.assetreason

import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result

interface DownloadAssetReasonUseCase {
    suspend operator fun invoke(): Result<List<Reason>>
}