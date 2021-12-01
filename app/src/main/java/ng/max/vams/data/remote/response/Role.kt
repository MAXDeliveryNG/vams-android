package ng.max.vams.data.remote.response

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("role_id")
    val roleId: String
)