package ng.max.vams.usecase.vehiclelist

import kotlinx.coroutines.flow.Flow
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.LoginData
import ng.max.vams.data.remote.response.VehicleListData
import ng.max.vams.data.wrapper.Result

interface VehicleListUseCase {
    suspend operator fun invoke(movementType: String): Flow<Result<List<DbVehicle>>>
}