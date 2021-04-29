package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("phone_verified")
    val phoneVerified: Boolean,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("uuid")
    val uuid: String
)