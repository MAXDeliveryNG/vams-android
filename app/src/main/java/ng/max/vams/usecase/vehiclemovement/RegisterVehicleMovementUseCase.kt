package ng.max.vams.usecase.vehiclemovement

import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RegisterVehicleMovementUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun invoke(movementBody: MovementBody): Result<Vehicle> {
        return remoteDataSource.registerMovement(movementBody)
    }
}