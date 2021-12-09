package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class LastVehicleMovementReason(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("parent_reason_id")
    val parentReasonId: String,
    @SerializedName("parent_reason_name")
    val parentReasonName: String
)