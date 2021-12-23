package ng.max.vams.usecase.vehiclelist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.DataMapper
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.local.dao.VehicleDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class VehicleListUseCase @Inject constructor(private val vehicleDao: VehicleDao,
                                             private val remoteDataSource: RemoteDataSource) {

    suspend fun invoke(): Flow<Result<List<DbVehicle>>> {
        var dbData: Flow<Result<List<DbVehicle>>> = vehicleDao.getAllVehicles().map {
            Result.Success(it)
        }


        when (val response = remoteDataSource.getUnconfirmedVehicles()){
            is Result.Error -> {
                dbData = flow {
                emit(Result.Error(response.message))
                }
            }
            is Result.Success -> {
                val vehicles = response.value.remoteVehicles.map {
                    DataMapper().invoke(it)
                }
                withContext(Dispatchers.IO){
                    vehicleDao.deleteVehicles() //remove on next deployment
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