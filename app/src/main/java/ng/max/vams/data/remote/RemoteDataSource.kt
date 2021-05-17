package ng.max.vams.data.remote

import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.*
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val vehicleService: VehicleService) {

    suspend fun getVehicles(movementType: String): Result<VehicleListData>{
        val response = vehicleService.getVehicleList(movementType)
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting vehicles ${response.code()} ${response.message()}")
    }

    suspend fun getLocations(): Result<List<Location>>{
        val response = vehicleService.getLocations()
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting Locations ${response.code()} ${response.message()}")
    }

    suspend fun getMovementStat(): Result<MovementStat>{
        val response = vehicleService.getMovementStat()
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting Stats ${response.code()} ${response.message()}")
    }

    suspend fun getVehicleType(): Result<List<VehicleType>>{
        val response = vehicleService.getVehicleType()
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting Vehicle Types ${response.code()} ${response.message()}")
    }

    suspend fun getMovementReasons(): Result<List<Reason>>{
        val response = vehicleService.getReasons()
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting movement seasons ${response.code()} ${response.message()}")
    }

    suspend fun getSearchResult(term: String): Result<VehicleListData>{
        val response = vehicleService.search(term)
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error getting search result ${response.code()} ${response.message()}")
    }

    suspend fun registerMovement(movementBody: MovementBody): Result<Vehicle>{
        val response = vehicleService.registerVehicleMovement(movementBody)
        if (response.isSuccessful) {
            val body =  response.body()
            if (body != null){
                return Result.Success(body.getData()!!)
            }
        }

        return Result.Error("Error registering movement ${response.code()} ${response.message()}")
    }
}