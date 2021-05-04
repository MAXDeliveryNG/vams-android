package ng.max.vams.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.Reason

@Dao
interface ReasonDao {

    @Query("SELECT * from Reason WHERE NOT slug = 'completed_hp'")
    fun getReasonsForCheckIn(): Flow<List<Reason>>

    @Query("SELECT * from Reason")
    fun getReasonsForCheckOut(): Flow<List<Reason>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReasons(reasons: List<Reason>)
}