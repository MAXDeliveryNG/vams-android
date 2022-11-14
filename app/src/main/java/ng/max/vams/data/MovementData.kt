package ng.max.vams.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ng.max.vams.BR
import ng.max.vams.data.remote.request.MovementBody

class MovementData : BaseObservable() {

    val keyLocationFrom: String = "location_from"
    val keyLocationTo: String = "locationTo"
    val keyOdometer: String = "odometer"
    val keyRetrievalAgent: String = "retrieval_agent"
    val keyReason: String = "reason"
    val keySubReason: String = "subreason"
    val keyAmountDefaulted: String = "amount_defaulted"

    @Bindable
    @SerializedName("parent_reason")
    var reason: String? = null
        set(value){
            field = value
            notifyPropertyChanged(BR.reason)
        }

    @Bindable
    @SerializedName("subreason")
    var subreason: String? = null
        set(value){
            field = value
            notifyPropertyChanged(BR.subreason)
        }

    @Bindable
    var location: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.location)
        }

    @Bindable
    var destLocation: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.destLocation)
        }

    @Bindable
    @SerializedName("odometer")
    var odometerReading: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.odometerReading)

        }

    @Bindable
    @SerializedName("amount_defaulted")
    var amountDefaulted: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.amountDefaulted)

        }

    @Bindable
    @SerializedName("retrieval_agent")
    var retrievalAgent: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.retrievalAgent)
        }

    @Bindable
    @SerializedName("recovered_items")
    var recoveredItems: ArrayList<String> = ArrayList()
        set(value) {
            field = value
            notifyPropertyChanged(BR.recoveredItems)
        }

    @Bindable
    @SerializedName("transfer_status")
    var transferStatus: String? = null
        set(value){
            field = value
            notifyPropertyChanged(BR.transferStatus)
        }

    @Bindable
    @SerializedName("vehicle_movement")
    var vehicleMovement: String? = null
        set(value){
            field = value
            notifyPropertyChanged(BR.vehicleMovement)
        }

    fun toMovementBody(): MovementBody {
        return Gson().fromJson(toJson(), MovementBody::class.java)
    }

    fun toJson(): String {
        return Gson().toJson(this, MovementData::class.java)
    }
}