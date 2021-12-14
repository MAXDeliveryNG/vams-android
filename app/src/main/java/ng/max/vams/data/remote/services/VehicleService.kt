package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.*
import retrofit2.Response
import retrofit2.http.*

interface VehicleService {

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleList(
        @Query("vehicle_movement") movementType: String,
        @Query("size") size: Int = 50,
        @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true,
        @Query("include_defined_relations") relations: String = "champion,vehicle_status"
    ): Response<ApiResponse<VehicleListData>>

    @GET("vehicles/v1/vehicle/movement/summary/count")
    suspend fun getMovementStat(): Response<ApiResponse<MovementStat>>

    @GET("vehicles/v1/vehicle/movement/reasons")
    suspend fun getReasons(): Response<ApiResponse<List<Reason>>>

    @GET("vehicles/v1/location")
    suspend fun getLocations(): Response<ApiResponse<List<Location>>>

    @GET("vehicles/v1/vehicle-type")
    suspend fun getVehicleType(): Response<ApiResponse<List<VehicleType>>>

    @GET("vehicles/v1/vehicle")
    suspend fun search(@Query("search_query") term: String,
                       @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true,
                       @Query("include_defined_relations") relations: String = "champion,vehicle_status"): Response<ApiResponse<VehicleListData>>

//    @GET("vehicles/v1/vehicle")
//    suspend fun searchVehicleWithReason(@Query("search_query") term: String,
//                                        @Query("vehicle_movement") movementType: String,
//                                        @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true,
//                                        @Query("include_defined_relations") relations: String = "champion,vehicle_status"): Response<ApiResponse<VehicleListData>>

    @POST("vehicles/v1/vehicle/movement/change")
    suspend fun registerVehicleMovement(@Body movementBody: MovementBody): Response<ApiResponse<Vehicle>>

    @GET("vehicles/v1/checklist")
    suspend fun recoveredItemsChecklist(): Response<ApiResponse<List<RetrivalChecklistItem>>>

    @GET("vehicles/v1/user-managment/getUser/{user_id}")
    suspend fun getUserRole(@Path("user_id")userId: String): Response<ApiResponse<RoleData>>

}