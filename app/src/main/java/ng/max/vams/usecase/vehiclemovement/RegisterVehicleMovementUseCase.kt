package ng.max.vams.usecase.vehiclemovement

import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.RemoteVehicle
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RegisterVehicleMovementUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun invoke(changeOption: String, movementBody: MovementBody): Result<RemoteVehicle> {
        return remoteDataSource.registerMovement(changeOption, movementBody)
    }
}