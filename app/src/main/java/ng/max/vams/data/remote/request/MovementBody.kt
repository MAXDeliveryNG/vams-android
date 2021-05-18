package ng.max.vams.data.remote.request

import com.google.gson.annotations.SerializedName

data class MovementBody (
    @SerializedName("vehicle_id")
    var vehicleId : String,
    @SerializedName("location_id")
    var locationId: Int?,
    @SerializedName("vehicle_movement")
    var movementType: String,
    @SerializedName("odometer")
    var odormeter: Double?,
    @SerializedName("reason")
    var reason: String)