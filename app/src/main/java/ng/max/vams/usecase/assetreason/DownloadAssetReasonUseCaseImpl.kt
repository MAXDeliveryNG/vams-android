package ng.max.vams.usecase.assetreason

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.ReasonDao
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class DownloadAssetReasonUseCaseImpl @Inject constructor(
    private val reasonDao: ReasonDao,
    private val vehicleService: VehicleService
) : DownloadAssetReasonUseCase {

    override suspend fun invoke(): Result<List<Reason>> {
        return try {
            val response = vehicleService.getReasons()
            if (response.isSuccessful) {
                val reasons = response.body()?.getData()
                withContext(Dispatchers.IO) {
                    saveReason(reasons!!)
                }
                Result.Success(reasons!!)
            } else {
                Result.Error(response.message())
            }

        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage!!)
        }
    }

    private fun saveReason(reasons: List<Reason>) {
        reasonDao.saveReasons(reasons)
    }
}