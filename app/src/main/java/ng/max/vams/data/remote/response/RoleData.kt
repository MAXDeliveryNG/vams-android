package ng.max.vams.data.remote.response

data class RoleData(
    val created_at: String,
    val deleted_at: Any,
    val email: String,
    val email_verified: Boolean,
    val first_name: String,
    val gender: Any,
    val last_name: String,
    val old_account: Boolean,
    val phone: String,
    val phone_verified: Boolean,
    val photo: String,
    val referral_applied: Boolean,
    val referral_code: String,
    val referrals: Int,
    val referrals_unused: Int,
    val role: Role,
    val status: String,
    val updated_at: Any,
    val user_account_id: String
)