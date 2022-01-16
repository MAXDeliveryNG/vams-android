package ng.max.vams.data


import com.google.gson.annotations.SerializedName

data class Manufacturer(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: Any?
)