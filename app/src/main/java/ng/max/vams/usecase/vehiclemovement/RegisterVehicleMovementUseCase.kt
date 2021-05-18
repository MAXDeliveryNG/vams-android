package ng.max.vams.usecase.vehiclemovement

import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RegisterVehicleMovementUseCase @Inject constructor(
    private val vehicleService: VehicleService
) {
    suspend fun invoke(movementBody: MovementBody): Result<Vehicle> {
        return try {
            val response = vehicleService.registerVehicleMovement(movementBody)
            if (response.isSuccessful){
                Result.Success(response.body()?.getData()!!)
            }else{
                Result.Error(response.message())
            }
        }catch (ex: Exception){
            Result.Error(ex.localizedMessage!!)
        }
    }
}