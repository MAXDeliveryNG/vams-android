package ng.max.vams.usecase.userrole

import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.response.RoleData
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class UserRoleUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource
    ) {
    suspend fun invokeRole(userId: String) : Result<RoleData>{
        return remoteDataSource.getUserRole(userId)
    }
}