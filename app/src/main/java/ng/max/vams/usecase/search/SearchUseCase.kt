package ng.max.vams.usecase.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ng.max.vams.data.remote.response.Vehicle
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val vehicleService: VehicleService
) {

    suspend fun invoke(term: String): Flow<Result<List<Vehicle>>> {
        return flow {
            try {
                val response = vehicleService.search(term)
                if (response.isSuccessful){
                    val vehicles = response.body()?.getData()?.vehicles
                    emit(Result.Success(vehicles!!))
                }
            }catch (ex: Exception){
                emit(Result.Error(ex.localizedMessage!!))
            }
        }
    }
}