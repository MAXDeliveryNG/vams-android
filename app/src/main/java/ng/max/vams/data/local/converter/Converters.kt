package ng.max.vams.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import ng.max.vams.data.remote.response.LastVehicleMovement

class Converters {
    @TypeConverter
    fun toLastVehicleMovement(value: String?): LastVehicleMovement? {
        return value?.let { Gson().fromJson(it, LastVehicleMovement::class.java) }
    }

    @TypeConverter
    fun fromLastVehicleMovement(lastVehicleMovement: LastVehicleMovement?): String? {
        return lastVehicleMovement?.let {
            Gson().toJson(it)
        }
    }

}