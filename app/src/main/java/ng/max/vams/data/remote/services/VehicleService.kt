package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.response.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehicleService {

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleList(@Query("vehicle_availability") availability: String,
                      @Query("size") size: Int = 50) : Response<ApiResponse<VehicleListData>>

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleListCount(@Query("vehicle_availability") availability: String) : Response<ApiResponse<VehicleListData>>

    @GET("vehicles/v1/vehicle/availability/reasons")
    suspend fun getReasons() : Response<ApiResponse<List<Reason>>>

}