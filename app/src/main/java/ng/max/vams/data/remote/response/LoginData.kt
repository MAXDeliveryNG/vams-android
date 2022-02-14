package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("user")
    val user: User,
)