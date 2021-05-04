package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

class ErrorResponse : ApiResponse<List<LoginErrorData>>()

data class DefaultErrorResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)