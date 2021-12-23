package ng.max.vams.data

import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.RemoteVehicle

class DataMapper {

    operator fun invoke(remoteVehicle: RemoteVehicle): DbVehicle{
        return DbVehicle(remoteVehicle.championId, remoteVehicle.createdAt, remoteVehicle.id,
            remoteVehicle.isMaxVehicle, remoteVehicle.licenseExpirationDate, remoteVehicle.locationId, remoteVehicle.manufacturerId,
            remoteVehicle.maxGlobalId, remoteVehicle.maxVehicleId, remoteVehicle.modelId, remoteVehicle.modelNumber, remoteVehicle.plateNumber,
            remoteVehicle.pricingTemplateId, remoteVehicle.serviceType, remoteVehicle.updatedAt, remoteVehicle.vehicleMovement, remoteVehicle.vehicleStatusId, remoteVehicle.vehicleTypeId,
            remoteVehicle.year, remoteVehicle.lastVehicleMovement, remoteVehicle.champion, remoteVehicle.status
        )
    }
}