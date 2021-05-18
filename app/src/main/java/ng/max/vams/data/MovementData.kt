package ng.max.vams.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ng.max.vams.BR
import ng.max.vams.data.remote.request.MovementBody

class MovementData : BaseObservable() {

    val keyVehiclePlateNumber: String = "plate_number"
    val keyLocation: String = "location"
    val keyVehicleType: String = "vehicle_type"
    val keyMovementType: String = "movement_type"
    val keyVehicleId: String = "vehicle_id"
    val keyOdometer: String = "odometer"
    val keyReason: String = "reason"

    @Bindable
    var plateNumber: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.plateNumber)
        }

    @Bindable
    var location: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.location)
        }

    @Bindable
    var vehicleType: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.vehicleType)
        }

    @Bindable
    var vehicleId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.vehicleId)
        }

    @Bindable
    @SerializedName("vehicle_movement")
    var movementType: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.movementType)
        }

    @Bindable
    @SerializedName("odometer")
    var odometerReading: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.odometerReading)
        }

    @Bindable
    @SerializedName("reason")
    var reason: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.reason)
        }

    fun toMovementBody(): MovementBody {
        return Gson().fromJson(toJson(), MovementBody::class.java)
    }

    fun toJson(): String {
        return Gson().toJson(this, MovementData::class.java)
    }
}