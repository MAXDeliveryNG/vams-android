package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.ApiEmpty
import ng.max.vams.data.remote.response.ApiResponse
import ng.max.vams.data.remote.response.LoginData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("v1/auths/login")
    suspend fun login(@Body loginBody: LoginBody) : Response<ApiResponse<LoginData>>

    @POST("accounts/auth/forgot-password")
    suspend fun requestForgotPassword(@Body body: HashMap<String, String>): Response<ApiResponse<ApiEmpty>>

    @POST("accounts/auth/reset-password")
    suspend fun requestPasswordReset(@Body body: HashMap<String, String>): Response<ApiResponse<ApiEmpty>>
}