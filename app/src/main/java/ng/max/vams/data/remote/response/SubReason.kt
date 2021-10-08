package ng.max.vams.data.remote.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubReason(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("explanation")
    val explanation: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("parent_id")
    val parentId: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String?
):Parcelable