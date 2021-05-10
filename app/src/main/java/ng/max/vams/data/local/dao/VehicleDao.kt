package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.local.DbVehicle

@Dao
interface VehicleDao {

    @Query("SELECT * from DbVehicle WHERE movementType = :movementType")
    fun getAllVehicles(movementType: String): Flow<List<DbVehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVehicles(vehicles: List<DbVehicle>)
}