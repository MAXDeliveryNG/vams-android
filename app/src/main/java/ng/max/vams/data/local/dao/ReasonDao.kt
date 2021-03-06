package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.Reason

@Dao
interface ReasonDao {

    @Query("SELECT * from Reason")
    fun getReasons(): Flow<List<Reason>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReasons(reasons: List<Reason>)

    @Query("SELECT * from Reason WHERE id = :reasonId")
    fun getReason(reasonId: Int): List<Reason>

    @Query("SELECT * from Reason WHERE name = :reasonName")
    fun getReasonItem(reasonName: String): List<Reason>

    @Query("DELETE from reason")
    suspend fun deleteAllReasons()
}