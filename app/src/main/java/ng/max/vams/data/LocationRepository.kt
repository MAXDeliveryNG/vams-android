package ng.max.vams.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.LocationDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class LocationRepository @Inject constructor(
        private val locationDao: LocationDao,
        private val remoteDataSource: RemoteDataSource) {

    suspend fun getLocations(): Flow<Result<List<Location>>>{
        var dbData = locationDao.getAllLocation().map {
            Result.Success(it)
        }

        when(val remoteData = remoteDataSource.getLocations()){
            is Result.Error -> {
                dbData = locationDao.getAllLocation().map {
                    Result.Success(it)
                }
            }
            Result.Loading -> { }
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    saveLocation(remoteData.value)
                }
            }
        }
        return dbData
    }

    private fun saveLocation(locations: List<Location>) {
        locationDao.saveLocation(locations)
    }

    suspend fun getLocationById(id: String) : Flow<Result<Location>>{
        var dbData: Flow<Result<Location>>
        withContext(Dispatchers.IO){
            dbData = locationDao.getLocationById(id).map {
                Result.Success(it)
            }
        }
        return  dbData
    }

    suspend fun getLocationByName(name: String) : Location?{
        var dbData: Location?
        withContext(Dispatchers.IO){
            dbData = locationDao.getLocationByName(name).first()
        }
        return  dbData
    }
}