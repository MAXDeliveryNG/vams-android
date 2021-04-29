package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginErrorData(
    @SerializedName("message")
    val message: String,
    @SerializedName("param")
    val `param`: String,
    @SerializedName("value")
    val value: String
)