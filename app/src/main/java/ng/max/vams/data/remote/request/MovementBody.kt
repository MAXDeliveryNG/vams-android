package ng.max.vams.data.remote.request

import com.google.gson.annotations.SerializedName

data class MovementBody (
    @SerializedName("vehicle_id")
    var vehicleId : String,
    @SerializedName("location_from_id")
    var locationFromId: Int?,
    @SerializedName("location_to_id")
    var locationToId: Int? = null,
    @SerializedName("vehicle_movement")
    var movementType: String,
    @SerializedName("odometer")
    var odormeter: Double?,
    @SerializedName("sub_reason_id")
    var subReasonId: Int)