package ng.max.vams.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ng.max.vams.data.local.dao.RetrievalChecklistDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RetrievalChecklistRepository @Inject constructor(
    private val retrievalChecklistDao: RetrievalChecklistDao,
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getRetrievalChecklistItems(): Flow<Result<List<RetrivalChecklistItem>>> {

        var dbData = retrievalChecklistDao.getRetrievalChecklistDb().map {
            Result.Success(it)
        }

        when (val remoteData = remoteDataSource.getRetrievalChecklist()) {
            is Result.Error -> {
                dbData = retrievalChecklistDao.getRetrievalChecklistDb().map {
                    Result.Success(it)
                }
            }
            Result.Loading -> {
            }
            is Result.Success -> {
                withContext(Dispatchers.IO) {
                    saveRetrievalChecklist(remoteData.value)
                }
            }
        }
        return dbData
    }

    private fun saveRetrievalChecklist(retrievalChecklistItem: List<RetrivalChecklistItem>) {
        retrievalChecklistDao.saveRetrievalChecklist(retrievalChecklistItem)
    }

    suspend fun getCheckListItems(): List<RetrivalChecklistItem>{
        var dbData: List<RetrivalChecklistItem>
        withContext(Dispatchers.IO){
            dbData = retrievalChecklistDao.getCheckListItems().toMutableList()
        }
        return  dbData
    }

}