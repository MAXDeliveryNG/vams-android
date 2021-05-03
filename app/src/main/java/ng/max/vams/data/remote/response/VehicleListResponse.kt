package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class VehicleListResponse(
    @SerializedName("data")
    val vehicleListData: VehicleListData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)