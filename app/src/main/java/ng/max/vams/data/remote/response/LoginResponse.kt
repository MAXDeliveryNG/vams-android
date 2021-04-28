package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val loginData: LoginData,
    @SerializedName("status")
    val status: String
)