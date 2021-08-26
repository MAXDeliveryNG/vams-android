package ng.max.vams.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.VehicleTypeDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.VehicleType
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class VehicleTypeRepository @Inject constructor(
        private val vehicleTypeDao: VehicleTypeDao,
        private val remoteDataSource: RemoteDataSource) {

    suspend fun getVehicleTypes(): Flow<Result<List<VehicleType>>> {
        var dbData = vehicleTypeDao.getAllVehicleType().map {
            Result.Success(it)
        }

        when(val remoteData = remoteDataSource.getVehicleType()){
            is Result.Error -> {
                dbData = vehicleTypeDao.getAllVehicleType().map {
                    Result.Success(it)
                }
            }
            Result.Loading -> { }
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    saveVehicleType(remoteData.value)
                }
            }
        }
        return dbData
    }

    private fun saveVehicleType(vehicleTypes: List<VehicleType>) {
        vehicleTypeDao.saveVehicleTypes(vehicleTypes)
    }

    suspend fun getVehicleTypeById(id: Int) : Result<VehicleType> {
        var dbData: Result<VehicleType>
        withContext(Dispatchers.IO){
            dbData = try {
                Result.Success(vehicleTypeDao.getVehicleType(id).first())
            }catch (ex: Exception){
                Result.Error("Vehicle Type with id $id not found.")
            }
        }
        return  dbData
    }
}