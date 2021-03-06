package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.local.DbVehicle

@Dao
interface VehicleDao {

    @Query("SELECT * from DbVehicle")
    fun getAllVehicles(): Flow<List<DbVehicle>>

    @Query("SELECT * from DbVehicle WHERE id = :id")
    fun getVehicleById(id: String): DbVehicle

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVehicles(vehicles: List<DbVehicle>)

        @Query("DELETE from DbVehicle WHERE id = :id")
    suspend fun deleteVehicle(id: String)

    @Query("DELETE from DbVehicle")
    suspend fun deleteVehicles()
}