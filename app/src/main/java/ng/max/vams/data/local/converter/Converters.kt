package ng.max.vams.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ng.max.vams.data.remote.response.Champion
import ng.max.vams.data.remote.response.LastVehicleMovement
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.data.remote.response.VehicleStatus

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

    @TypeConverter
    fun toChampion(value: String?): Champion? {
        return value?.let { Gson().fromJson(it, Champion::class.java) }
    }

    @TypeConverter
    fun fromChampion(champion: Champion?): String? {
        return champion?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun toVehicleStatus(value: String?): VehicleStatus? {
        return value?.let { Gson().fromJson(it, VehicleStatus::class.java) }
    }

    @TypeConverter
    fun fromVehicleStatus(vehicleStatus: VehicleStatus?): String? {
        return vehicleStatus?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun toSubReasonList(value: String?): List<SubReason>? {
        val listType = object : TypeToken<List<SubReason>>() {}.type
        return value?.let { Gson().fromJson(it, listType) }
    }

    @TypeConverter
    fun fromSubReasonList(subReason: List<SubReason>?): String? {
        return subReason?.let {
            Gson().toJson(it)
        }
    }


}