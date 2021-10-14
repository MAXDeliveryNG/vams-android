package ng.max.vams.data.remote.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubReason(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("definition")
    val definition: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("parent_id")
    val parentId: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String?
):Parcelable