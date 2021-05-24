package ng.max.vams.data

import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.Vehicle

class DataMapper {

    operator fun invoke(vehicle: Vehicle): DbVehicle{
        return DbVehicle(vehicle.championId, vehicle.createdAt, vehicle.id,
            vehicle.isMaxVehicle, vehicle.licenseExpirationDate, vehicle.locationId, vehicle.manufacturerId,
            vehicle.maxGlobalId, vehicle.maxVehicleId, vehicle.modelId, vehicle.modelNumber, vehicle.plateNumber,
            vehicle.pricingTemplateId, vehicle.serviceType, vehicle.updatedAt, vehicle.vehicleMovement, vehicle.vehicleStatusId, vehicle.vehicleTypeId,
            vehicle.year, vehicle.lastVehicleMovement
        )
    }
}