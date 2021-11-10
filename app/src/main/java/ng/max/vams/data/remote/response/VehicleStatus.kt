package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class VehicleStatus(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)