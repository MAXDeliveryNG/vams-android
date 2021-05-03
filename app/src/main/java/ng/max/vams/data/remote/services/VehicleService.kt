package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.LoginResponse
import ng.max.vams.data.remote.response.VehicleListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VehicleService {

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleList(@Query("vehicle_availability") availability: String,
                      @Query("size") size: Int = 50) : Response<VehicleListResponse>

    @GET("vehicles/v1/vehicle")
    suspend fun getVehicleListCount(@Query("vehicle_availability") availability: String) : Response<VehicleListResponse>

}