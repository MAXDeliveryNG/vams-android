package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VehicleService {

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleList(
        @Query("vehicle_movement") movementType: String,
        @Query("size") size: Int = 50,
        @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true
    ): Response<ApiResponse<VehicleListData>>

    @GET("vehicles/v1/vehicle/movement/summary/count")
    suspend fun getMovementStat(): Response<ApiResponse<MovementStat>>

    @GET("vehicles/v1/vehicle/movement/reasons")
    suspend fun getReasons(): Response<ApiResponse<List<Reason>>>

    @GET("vehicles/v1/location")
    suspend fun getLocations(): Response<ApiResponse<List<Location>>>

    @GET("vehicles/v1/vehicle-type")
    suspend fun getVehicleType(): Response<ApiResponse<List<VehicleType>>>

    @GET("vehicles/v1/vehicle/search")
    suspend fun search(@Query("search_query") term: String): Response<ApiResponse<VehicleListData>>

    @POST("vehicles/v1/vehicle/movement/change")
    suspend fun registerVehicleMovement(@Body movementBody: MovementBody): Response<ApiResponse<Vehicle>>

}