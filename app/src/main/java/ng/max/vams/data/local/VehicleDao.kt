package ng.max.vams.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Query("SELECT * from DbVehicle WHERE vehicleAvailability = :availability")
    fun getAllVehicles(availability: String): Flow<List<DbVehicle>>

    @Insert
    fun saveVehicles(vehicles: List<DbVehicle>)
}