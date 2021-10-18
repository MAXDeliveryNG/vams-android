package ng.max.vams.data.remote.response


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Reason(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("reason")
    val name: String,
    @SerializedName("definition")
    val definition: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("subReasons")
    val subReasons: List<SubReason>?,
): Parcelable