package ng.max.vams.data.remote.request

import com.google.gson.annotations.SerializedName

data class MovementBody (
    @SerializedName("vehicle_id")
    var vehicleId : String,
    @SerializedName("location_from_id")
    var locationFromId: String?,
    @SerializedName("location_to_id")
    var locationToId: String?,
    @SerializedName("sub_reason_id")
    var subReasonId: String,
    @SerializedName("recovered_items")
    var recoveredItems: List<String>,
    @SerializedName("retrieval_agent")
    var retrievalAgent: String?,
    @SerializedName("amount_defaulted")
    var amountDefaulted: Double?,
    @SerializedName("transfer_status")
    var transferStatus: String? = null,
    @SerializedName("vehicle_movement")
    var vehicleMovement: String? = null,
    @SerializedName("odometer")
    var odormeter: Double?
    )