package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.RetrivalChecklistItem

@Dao
interface RetrievalChecklistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRetrievalChecklist(retrivalChecklist: List<RetrivalChecklistItem>)

    @Query("SELECT * FROM RetrivalChecklistItem")
    fun getRetrievalChecklistDb(): Flow<List<RetrivalChecklistItem>>

    @Query("SELECT * FROM RetrivalChecklistItem")
    fun getCheckListItems(): List<RetrivalChecklistItem>


}