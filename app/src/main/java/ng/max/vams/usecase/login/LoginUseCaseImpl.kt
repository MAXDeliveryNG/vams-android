package ng.max.vams.usecase.login

import com.google.gson.Gson
import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.DefaultErrorResponse
import ng.max.vams.data.remote.response.ErrorResponse
import ng.max.vams.data.remote.response.LoginData
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val userService: UserService): LoginUseCase {

    override suspend fun invoke(loginBody: LoginBody): Result<LoginData> {
        return try {
            val response = userService.login(loginBody)
            return if (response.isSuccessful){
                Result.Success(response.body()?.getData()!!)
            }else{
                val errorResponse = response.errorBody()?.string()!!
                try {
                    val message = Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                    Result.Error(message)
                }catch (ex: Exception){
                    val message = Gson().fromJson(errorResponse, ErrorResponse::class.java).getData()?.first()?.message!!
                    Result.Error(message)
                }
            }
        }catch (ex: Exception){
            Result.Error(ex.localizedMessage!!)
        }
    }
}