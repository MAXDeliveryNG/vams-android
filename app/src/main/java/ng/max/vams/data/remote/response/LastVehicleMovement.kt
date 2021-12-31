package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName

data class LastVehicleMovement(
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("creator_email")
    val creatorEmail: String?,
    @SerializedName("creator_id")
    val creatorId: String?,
    @SerializedName("creator_name")
    val creatorName: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("location_to_id")
    val locationToId: String?,
    @SerializedName("location_from_id")
    val locationFromId: String?,
    @SerializedName("location_name")
    val locationName: String,
    @SerializedName("movement_type")
    val movementType: String,
    @SerializedName("movement_type_from")
    val movementTypeFrom: String?,
    @SerializedName("odometer")
    val odometer: Double,
    @SerializedName("plate_numer")
    val plateNumber: String,
    @SerializedName("reason")
    val reason: LastVehicleMovementReason,
    @SerializedName("checklist_items")
    val checkListItems: List<String>?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("vehicle_type")
    val vehicleType: String,
    @SerializedName("transfer_status")
    val transferStatus: String?,
    @SerializedName("amount_defaulted")
    val amountDefaulted: String?,
    @SerializedName("retrieval_agent")
    val retrievalAgent: String?,
    @SerializedName("location_from_name")
    val locationFromName: String?,
    @SerializedName("location_to_name")
    val locationToName: String?

)