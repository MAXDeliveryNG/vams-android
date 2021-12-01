package ng.max.vams.data.remote.response

import com.google.gson.annotations.SerializedName

data class RoleData(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("deleted_at")
    val deletedAt: Any,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: Any,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("old_account")
    val oldAccount: Boolean,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("phone_verified")
    val phoneVerified: Boolean,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("referral_applied")
    val referralApplied: Boolean,
    @SerializedName("referral_code")
    val referralCode: String,
    @SerializedName("referrals")
    val referrals: Int,
    @SerializedName("referrals_unused")
    val referralsUnused: Int,
    @SerializedName("role")
    val role: Role,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: Any,
    @SerializedName("user_account_id")
    val userAccountId: String
)