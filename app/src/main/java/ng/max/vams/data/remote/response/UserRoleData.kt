package ng.max.vams.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserRoleData(
    @SerializedName("data")
    val `data`: RoleData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)