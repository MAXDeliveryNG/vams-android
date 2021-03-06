package ng.max.vams.data.remote.response


import com.google.gson.annotations.SerializedName
import ng.max.vams.data.Manufacturer

data class RemoteVehicle(
    @SerializedName("batch_id")
    val batchId: Int,
    @SerializedName("champion_id")
    val championId: String?,
    @SerializedName("chassis_no")
    val chassisNo: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("device_imei")
    val deviceImei: String,
    @SerializedName("device_phone")
    val devicePhone: String,
    @SerializedName("documents_file_names_csv")
    val documentsFileNamesCsv: String?,
    @SerializedName("engine_number")
    val engineNumber: String,
    @SerializedName("hpv_id")
    val hpvId: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("is_max_vehicle")
    val isMaxVehicle: Boolean,
    @SerializedName("last_vehicle_movement")
    val lastVehicleMovement: LastVehicleMovement?,
    @SerializedName("license_expiration_date")
    val licenseExpirationDate: String,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("manufacturer_id")
    val manufacturerId: Int,
    @SerializedName("max_global_id")
    val maxGlobalId: String,
    @SerializedName("max_vehicle_id")
    val maxVehicleId: String,
    @SerializedName("model_id")
    val modelId: Int,
    @SerializedName("model_number")
    val modelNumber: String?,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("pricing_template_id")
    val pricingTemplateId: String?,
    @SerializedName("pv_id")
    val pvId: Int,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("sim_network_id")
    val simNetworkId: Int,
    @SerializedName("sim_serial_no")
    val simSerialNo: String,
    @SerializedName("trim_id")
    val trimId: Int,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("vehicle_movement")
    val vehicleMovement: String?,
    @SerializedName("vehicle_status_id")
    val vehicleStatusId: String?,
    @SerializedName("vehicle_type_id")
    val vehicleTypeId: Int,
    @SerializedName("year")
    val year: Int?,
    @SerializedName("champion")
    val champion: Champion?,
    @SerializedName("vehicle_status")
    val status: VehicleStatus?,
    @SerializedName("manufacturer")
    val manufacturer: Manufacturer?
)