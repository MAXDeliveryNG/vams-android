package ng.max.vams.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.RetrivalChecklistDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.wrapper.Result
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import javax.inject.Inject

class RetrivalChecklistRepository @Inject constructor(
    private val retrivalChecklistDao: RetrivalChecklistDao,
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getRetrivalChecklistItems(): Flow<Result<List<RetrivalChecklistItem>>> {

        var dbData = retrivalChecklistDao.getRetrivalChecklistDb().map {
            Result.Success(it)
        }

        when (val remoteData = remoteDataSource.getRetrivalChecklist()) {
            is Result.Error -> {
                dbData = retrivalChecklistDao.getRetrivalChecklistDb().map {
                    Result.Success(it)
                }
            }
            Result.Loading -> {
            }
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    saveRetrivalChecklist(remoteData.value)
                }
            }
        }
        return dbData
    }

    private fun saveRetrivalChecklist(retrivalChecklistItem: List<RetrivalChecklistItem>) {
        Log.d("THEVSULE", "THE VALUE $retrivalChecklistItem")
        retrivalChecklistDao.saveRetrivalChecklist(retrivalChecklistItem)
    }

}