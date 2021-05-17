package ng.max.vams.usecase.vehiclelist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.dao.VehicleDao
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class VehicleListUseCase @Inject constructor(private val vehicleDao: VehicleDao,
                                             private val vehicleService: VehicleService) {

    suspend fun invoke(movementType: String): Flow<Result<List<DbVehicle>>> {
        var dbData: Flow<Result<List<DbVehicle>>> = vehicleDao.getAllVehicles(movementType).map {
            Result.Success(it)
        }
        try {
            val response = vehicleService.getVehicleList(movementType)
            if (response.isSuccessful) {
                val vehicles = response.body()?.getData()?.vehicles?.map {
                    DbVehicle(
                            it.batchId, it.championId, it.chassisNo, it.createdAt, it.deviceImei,
                            it.devicePhone, it.documentsFileNamesCsv, it.engineNumber, it.hpvId, it.id,
                            it.isMaxVehicle, it.licenseExpirationDate, it.locationId, it.manufacturerId,
                            it.maxGlobalId, it.maxVehicleId, it.modelId, it.modelNumber, it.plateNumber,
                            it.pricingTemplateId, it.pvId, it.serviceType, it.simNetworkId, it.simSerialNo,
                            it.trimId, it.updatedAt, it.movementType, it.movementReason, it.vehicleStatusId, it.vehicleTypeId,
                            it.year
                    )
                }
                withContext(Dispatchers.IO){
                    saveVehicles(vehicles!!)
                }
            }

        } catch (ex: Exception) {
            dbData = flow {
                emit(Result.Error(ex.localizedMessage!!))
            }
        }
        return dbData
    }

    private fun saveVehicles(vehicles: List<DbVehicle>) {
        vehicleDao.saveVehicles(vehicles)
    }
}