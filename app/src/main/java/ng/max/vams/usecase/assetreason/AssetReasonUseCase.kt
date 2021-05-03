package ng.max.vams.usecase.assetreason

import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result

interface AssetReasonUseCase {
    suspend operator fun invoke(availability: String): Flow<Result<List<Reason>>>
}