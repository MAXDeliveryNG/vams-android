package ng.max.vams.data.remote.response

import com.google.gson.annotations.SerializedName

open class ApiResponse<T> {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null
        get() {
            return field ?: ""
        }

    fun hasData(): Boolean {
        return responseData != null
    }

    @SerializedName("data")
    open var responseData: T? = null

    fun getData(): T? {
        return responseData
    }
}