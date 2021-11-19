package ng.max.vams.data.remote.request

import com.google.gson.annotations.SerializedName

data class MovementBody (
    @SerializedName("vehicle_id")
    var vehicleId : String,
    @SerializedName("location_from_id")
    var locationFromId: String?,
    @SerializedName("location_to_id")
    var locationToId: String?,
    @SerializedName("odometer")
    var odormeter: Double?,
    @SerializedName("sub_reason_id")
    var subReasonId: String,
    @SerializedName("recovered_items")
    var recoveredItems: List<String>,
    @SerializedName("retrieval_agent")
    var retrievalAgent: String?,
    )