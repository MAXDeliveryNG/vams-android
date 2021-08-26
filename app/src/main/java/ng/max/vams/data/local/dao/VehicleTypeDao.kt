package ng.max.vams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.VehicleType

@Dao
interface VehicleTypeDao {

    @Query("SELECT * from vehicletype WHERE id = :id")
    fun getVehicleType(id: Int): List<VehicleType>

    @Query("SELECT * from vehicletype")
    fun getAllVehicleType(): Flow<List<VehicleType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVehicleTypes(vehicleTypes: List<VehicleType>)
}