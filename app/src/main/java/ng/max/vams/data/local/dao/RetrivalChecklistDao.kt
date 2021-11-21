package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.RetrivalChecklistItem

@Dao
interface RetrivalChecklistDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveRetrivalChecklist(retrivalChecklist: List<RetrivalChecklistItem>)

        @Query("SELECT * FROM RetrivalChecklistItem")
        fun getRetrivalChecklistDb() : Flow<List<RetrivalChecklistItem>>


}