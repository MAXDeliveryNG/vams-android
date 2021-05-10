package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class MovementStat(
    @SerializedName("overallTotal")
    val overallTotal: Int,
    @SerializedName("totalEntry")
    val totalEntry: Int,
    @SerializedName("totalExit")
    val totalExit: Int
)