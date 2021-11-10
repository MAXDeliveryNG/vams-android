package ng.max.vams.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.ReasonDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class MovementReasonRepository @Inject constructor(
        private val reasonDao: ReasonDao,
        private val remoteDataSource: RemoteDataSource) {

    suspend fun getMovementReasons(): Flow<Result<List<Reason>>> {
        var dbData = reasonDao.getReasons().map {
            Result.Success(it)
        }

        when(val remoteData = remoteDataSource.getMovementReasons()){
            is Result.Error -> {
                dbData = reasonDao.getReasons().map {
                    Result.Success(it)
                }
            }
            Result.Loading -> { }
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    saveMovementReasons(remoteData.value)
                }
            }
        }
        return dbData
    }

    private fun saveMovementReasons(reasons: List<Reason>) {
        reasonDao.saveReasons(reasons)
    }

    suspend fun getMovementReasonById(id: Int) : Result<Reason> {
        var dbData: Result<Reason>
        withContext(Dispatchers.IO){
            dbData = try {
                Result.Success(reasonDao.getReason(id).first())
            }catch (ex: Exception){
                Result.Error("Movement reason with id $id not found.")
            }
        }
        return  dbData
    }
}