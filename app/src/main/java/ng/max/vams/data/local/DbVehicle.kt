package ng.max.vams.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ng.max.vams.data.remote.response.Champion
import ng.max.vams.data.remote.response.LastVehicleMovement
import ng.max.vams.data.remote.response.VehicleStatus

@Entity
data class DbVehicle(
        val championId: String?,
        val createdAt: String?,
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
        val serviceType: String?,
        val updatedAt: String?,
        val movementType: String?,
        val vehicleStatusId: String?,
        val vehicleTypeId: Int?,
        val year: Int?,
        val lastVehicleMovement: LastVehicleMovement?,
        val champion: Champion?,
        val status: VehicleStatus?
)