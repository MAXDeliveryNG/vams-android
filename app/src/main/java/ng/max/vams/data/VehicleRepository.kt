package ng.max.vams.data

import ng.max.vams.data.local.dao.VehicleDao
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao) {

    suspend fun deleteVehicle(id: String){
        vehicleDao.deleteVehicle(id)
    }

    suspend fun deleteVehicleData() {
        vehicleDao.deleteVehicles()
    }
}