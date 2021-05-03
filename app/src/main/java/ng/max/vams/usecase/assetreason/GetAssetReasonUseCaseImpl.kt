package ng.max.vams.usecase.assetreason

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ng.max.vams.data.local.ReasonDao
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class GetAssetReasonUseCaseImpl @Inject constructor(
    private val reasonDao: ReasonDao,
    private val downloadAssetReasonUseCaseImpl: DownloadAssetReasonUseCaseImpl
) : AssetReasonUseCase {
    override suspend fun invoke(availability: String): Flow<Result<List<Reason>>> {
        val dbData = if (availability == "checked_in") {
            reasonDao.getReasonsForCheckIn().map {
                Result.Success(it)
            }
        } else {
            reasonDao.getReasonsForCheckOut().map {
                Result.Success(it)
            }
        }

        downloadAssetReasonUseCaseImpl.invoke()

        return dbData
    }
}