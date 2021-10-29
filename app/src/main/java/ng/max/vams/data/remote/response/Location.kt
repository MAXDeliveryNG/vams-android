package ng.max.vams.data.remote.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Location(
    @SerializedName("code")
    val code: String,
    @SerializedName("created_at")
    val createdAt: String,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("city_id")
    val cityId: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)