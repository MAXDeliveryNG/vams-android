package ng.max.vams.data.remote.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Reason(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("explanation")
    val explanation: String?
)