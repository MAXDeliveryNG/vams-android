package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("data")
    val loginErrorData: List<LoginErrorData>,
    @SerializedName("status")
    val status: String
)

data class DefaultErrorResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)