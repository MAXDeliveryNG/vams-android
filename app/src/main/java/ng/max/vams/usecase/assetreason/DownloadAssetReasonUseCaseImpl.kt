package ng.max.vams.usecase.assetreason

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.ReasonDao
import ng.max.vams.data.local.VehicleDao
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class DownloadAssetReasonUseCaseImpl @Inject constructor(
    private val reasonDao: ReasonDao,
    private val vehicleService: VehicleService
) : DownloadAssetReasonUseCase {

    override suspend fun invoke(){
        try {
            val response = vehicleService.getReasons()
            if (response.isSuccessful) {
                val reasons = response.body()?.reasons
                withContext(Dispatchers.IO) {
                    saveReason(reasons!!)
                }
            }

        } catch (ex: Exception) {
            val error = ex.localizedMessage
        }
    }

    private fun saveReason(reasons: List<Reason>) {
        reasonDao.saveReasons(reasons)
    }
}