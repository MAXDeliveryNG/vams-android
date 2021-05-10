package ng.max.vams.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DbVehicle(
        val batchId: Int,
        val championId: String?,
        val chassisNo: String?,
        val createdAt: String?,
        val deviceImei: String?,
        val devicePhone: String?,
        val documentsFileNamesCsv: String?,
        val engineNumber: String?,
        val hpvId: String?,
        @PrimaryKey
        val id: String,
        val isMaxVehicle: Boolean,
        val licenseExpirationDate: String?,
        val locationId: Int?,
        val manufacturerId: Int?,
        val maxGlobalId: String?,
        val maxVehicleId: String?,
        val modelId: Int?,
        val modelNumber: String?,
        val plateNumber: String?,
        val pricingTemplateId: String?,
        val pvId: String?,
        val serviceType: String?,
        val simNetworkId: String?,
        val simSerialNo: String?,
        val trimId: Int?,
        val updatedAt: String?,
        val movementType: String?,
        val movementReason: String?,
        val vehicleStatusId: String?,
        val vehicleTypeId: Int?,
        val year: String?
)