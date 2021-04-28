package ng.max.vams.data.remote.services

import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("v1/auths/login")
    suspend fun login(@Body loginBody: LoginBody) : Response<LoginResponse>
}