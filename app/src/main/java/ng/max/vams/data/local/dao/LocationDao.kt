package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.remote.response.Location

@Dao
interface LocationDao {

    @Query("SELECT * from location WHERE id = :id")
    fun getLocationById(id: String): Flow<Location>

    @Query("SELECT * from location WHERE name = :name")
    fun getLocationByName(name: String): List<Location>

    @Query("SELECT * from location ")
    fun getAllLocation(): Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocation(location: List<Location>)
}