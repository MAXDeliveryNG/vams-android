package ng.max.vams.usecase.vehiclelist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.VehicleDao
import ng.max.vams.data.remote.response.VehicleListData
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class VehicleListUseCaseImpl @Inject constructor(private val vehicleDao: VehicleDao,
                                                 private val vehicleService: VehicleService) : VehicleListUseCase {

    override suspend fun invoke(availability: String): Flow<Result<List<DbVehicle>>> {
        var dbData = vehicleDao.getAllVehicles(availability).map {
            Result.Success(it)
        }
        try {
            val response = vehicleService.getVehicleList(availability)
            if (response.isSuccessful) {
                val vehicles = response.body()?.vehicleListData?.vehicles?.map {
                    DbVehicle(
                            it.batchId, it.championId, it.chassisNo, it.createdAt, it.deviceImei,
                            it.devicePhone, it.documentsFileNamesCsv, it.engineNumber, it.hpvId, it.id,
                            it.isMaxVehicle, it.licenseExpirationDate, it.locationId, it.manufacturerId,
                            it.maxGlobalId, it.maxVehicleId, it.modelId, it.modelNumber, it.plateNumber,
                            it.pricingTemplateId, it.pvId, it.serviceType, it.simNetworkId, it.simSerialNo,
                            it.trimId, it.updatedAt, it.vehicleAvailability, it.vehicleStatusId, it.vehicleTypeId,
                            it.year
                    )
                }
                withContext(Dispatchers.IO){
                    saveVehicles(vehicles!!)
                }
            }

        } catch (ex: Exception) {
            val error = ex.localizedMessage
            dbData = vehicleDao.getAllVehicles(availability).map {
                Result.Success(it)
            }
        }
        return dbData
    }

    private fun saveVehicles(vehicles: List<DbVehicle>) {
        vehicleDao.saveVehicles(vehicles)
    }
}