package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class VehicleListData(
    @SerializedName("pagination")
    val pagination: Pagination,
    @SerializedName("results")
    val vehicles: List<Vehicle>
)