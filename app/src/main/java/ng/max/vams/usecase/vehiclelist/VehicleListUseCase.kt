package ng.max.vams.usecase.vehiclelist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.dao.VehicleDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class VehicleListUseCase @Inject constructor(private val vehicleDao: VehicleDao,
                                             private val remoteDataSource: RemoteDataSource) {

    suspend fun invoke(movementType: String): Flow<Result<List<DbVehicle>>> {
        var dbData: Flow<Result<List<DbVehicle>>> = vehicleDao.getAllVehicles(movementType).map {
            Result.Success(it)
        }


        when (val response = remoteDataSource.getVehicles(movementType)){
            is Result.Error -> {
                dbData = flow {
                emit(Result.Error(response.message))
                }
            }
            is Result.Success -> {
                val vehicles = response.value.vehicles.map {
                    DbVehicle(it.championId, it.createdAt, it.id,
                        it.isMaxVehicle, it.licenseExpirationDate, it.locationId, it.manufacturerId,
                        it.maxGlobalId, it.maxVehicleId, it.modelId, it.modelNumber, it.plateNumber,
                        it.pricingTemplateId, it.serviceType, it.updatedAt, it.vehicleMovement, it.vehicleStatusId, it.vehicleTypeId,
                        it.year, it.lastVehicleMovement
                    )
                }
                withContext(Dispatchers.IO){
                    saveVehicles(vehicles)
                }
            }
        }

        return dbData
    }

    private fun saveVehicles(vehicles: List<DbVehicle>) {
        vehicleDao.saveVehicles(vehicles)
    }
}