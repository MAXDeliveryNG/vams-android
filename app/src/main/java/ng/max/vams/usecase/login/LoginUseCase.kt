package ng.max.vams.usecase.login

import ng.max.vams.data.remote.request.LoginBody
import ng.max.vams.data.remote.response.LoginData
import ng.max.vams.data.remote.response.LoginResponse
import ng.max.vams.data.wrapper.Result

interface LoginUseCase {
    suspend operator fun invoke(loginBody: LoginBody): Result<LoginData>
}