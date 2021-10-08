package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class Champion(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("bank_account_name")
    val bankAccountName: String,
    @SerializedName("bank_account_number")
    val bankAccountNumber: String,
    @SerializedName("bank_name")
    val bankName: String,
    @SerializedName("bvn")
    val bvn: String,
    @SerializedName("champion_status")
    val championStatus: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("contractor_id")
    val contractorId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("date_disengaged")
    val dateDisengaged: Any,
    @SerializedName("date_engaged")
    val dateEngaged: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("deleted_at")
    val deletedAt: Any,
    @SerializedName("delivery_service_id")
    val deliveryServiceId: Any,
    @SerializedName("emergency_contact_name")
    val emergencyContactName: String,
    @SerializedName("emergency_contact_number")
    val emergencyContactNumber: String,
    @SerializedName("helmet_number")
    val helmetNumber: String,
    @SerializedName("house_address")
    val houseAddress: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("marital_status")
    val maritalStatus: String,
    @SerializedName("max_champion_id")
    val maxChampionId: String,
    @SerializedName("meta")
    val meta: Any,
    @SerializedName("next_of_kin_name")
    val nextOfKinName: String,
    @SerializedName("next_of_kin_phone")
    val nextOfKinPhone: String,
    @SerializedName("package_id")
    val packageId: Any,
    @SerializedName("phone_brand")
    val phoneBrand: String,
    @SerializedName("phone_ime_number")
    val phoneImeNumber: String,
    @SerializedName("prospective_champion_id")
    val prospectiveChampionId: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("registration_number")
    val registrationNumber: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("state_of_birth")
    val stateOfBirth: String,
    @SerializedName("state_of_origin")
    val stateOfOrigin: String,
    @SerializedName("updated_at")
    val updatedAt: String
)