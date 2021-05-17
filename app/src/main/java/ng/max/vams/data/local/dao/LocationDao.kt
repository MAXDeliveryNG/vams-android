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
    fun getLocationById(id: Int): List<Location>

    @Query("SELECT * from location WHERE name = :name")
    fun getLocationByName(name: String): List<Location>

    @Query("SELECT * from location WHERE code != 'KA' AND code != 'EK' AND code != 'BI' AND code != 'IB' AND code != 'LA'")
    fun getAllLocation(): Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocation(location: List<Location>)
}