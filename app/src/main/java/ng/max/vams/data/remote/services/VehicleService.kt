package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.*
import retrofit2.Response
import retrofit2.http.*

interface VehicleService {

    @GET("vehicles/v1/vehicle")
    suspend fun getUnconfirmedVehicleList(
        @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true,
        @Query("include_defined_relations") relations: String = "champion,vehicle_status,manufacturer",
        @Query("unconfirmed_vehicles") unconfirmed: Boolean = true
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
    suspend fun search(@Query("search_value") term: String,
                       @Query("search_column") searchColumnTitle: String = "plate_number",
                       @Query("include_vehicle_movement_and_last_reason") withReason: Boolean = true,
                       @Query("include_defined_relations") relations: String = "champion,vehicle_status,manufacturer"): Response<ApiResponse<VehicleListData>>

    @POST("vehicles/v1/vehicle/movement/{movement_option}")
    suspend fun registerVehicleMovement(
        @Path("movement_option")changeOption: String,
        @Body movementBody: MovementBody): Response<ApiResponse<RemoteVehicle>>

    @GET("vehicles/v1/checklist/firestore")
    suspend fun recoveredItemsChecklist(): Response<ApiResponse<List<RetrivalChecklistItem>>>

    @GET("vehicles/v1/user-managment/getUser/{user_id}")
    suspend fun getUserRole(@Path("user_id")userId: String): Response<ApiResponse<RoleData>>

    @GET("vehicles/v1/vehicle/movement/summary")
    suspend fun getFullMovementStat(@Query("agent_id")userId: String): Response<ApiResponse<FullMovementStat>>

    @POST("vehicles/v1/vams-notification/save-location")
    suspend fun saveUserLocation(@Body locationBody: HashMap<String, Any>): Response<ApiResponse<Any>>

    @POST("vehicles/v1/vams-notification/save-registration-token")
    suspend fun saveToken(@Body token: HashMap<String, String>): Response<ApiResponse<Any>>

}