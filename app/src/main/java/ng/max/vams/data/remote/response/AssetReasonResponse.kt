package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class AssetReasonResponse(
    @SerializedName("data")
    val reasons: List<Reason>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)