package ng.max.vams.data.remote

import com.google.gson.Gson
import ng.max.vams.data.remote.request.MovementBody
import ng.max.vams.data.remote.response.*
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.data.wrapper.Result
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val userService: UserService,
    private val vehicleService: VehicleService
) {

    suspend fun forgotPasswordRequest(email: String): Result<ApiEmpty> {
        try {
            val requestBody = java.util.HashMap<String, String>().apply {
                this["username"] = email
            }
            val response = userService.requestForgotPassword(requestBody)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error sending temp password ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun resetPasswordRequest(
        userEmail: String,
        tempPassword: String,
        newPassword: String
    ): Result<ApiEmpty> {
        try {
            val requestBody = java.util.HashMap<String, String>().apply {
                this["username"] = userEmail
                this["temporary_password"] = tempPassword
                this["new_password"] = newPassword
            }
            val response = userService.requestPasswordReset(requestBody)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error resetting password ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getUnconfirmedVehicles(): Result<VehicleListData> {
        try {
            val response = vehicleService.getUnconfirmedVehicleList()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error getting vehicles ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getLocations(): Result<List<Location>> {
        try {
            val response = vehicleService.getLocations()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.responseData!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error getting Locations ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getRetrievalChecklist(): Result<List<RetrivalChecklistItem>> {
        try {
            val response = vehicleService.recoveredItemsChecklist()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.responseData!!)
                }
            }
            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error getting Checklist Data ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getUserRole(userId: String): Result<RoleData> {
        return try {
            val response = vehicleService.getUserRole(userId)
            return if (response.isSuccessful) {
                Result.Success(response.body()?.getData()!!)
            } else {
                val errorResponse = response.errorBody()?.string()!!
                try {
                    val message =
                        Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                    Result.Error(message)
                } catch (ex: Exception) {
                    val message =
                        Gson().fromJson(errorResponse, ErrorResponse::class.java).getData()
                            ?.first()?.message!!
                    Result.Error(message)
                }
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getVehicleType(): Result<List<VehicleType>> {
        try {
            val response = vehicleService.getVehicleType()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                Result.Error("Error getting Vehicle Types ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getMovementReasons(): Result<List<Reason>> {
        try {
            val response = vehicleService.getReasons()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData() ?: emptyList())
                }
            }
            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                Result.Error("Error getting movement seasons ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun getSearchResult(term: String): Result<List<RemoteVehicle>> {
        try {
            val response = vehicleService.search(term)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()?.remoteVehicles!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                Result.Error("Error getting search result ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun registerMovement(changeOption: String, movementBody: MovementBody): Result<RemoteVehicle> {
        try {
            val response = vehicleService.registerVehicleMovement(changeOption, movementBody)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                Result.Error("Error registering movement ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun changePasswordRequest(
        userEmail: String,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<ApiEmpty> {
        try {
            val requestBody = java.util.HashMap<String, String>().apply {
                this["email"] = userEmail
                this["old_password"] = currentPassword
                this["new_password"] = newPassword
                this["new_password_confirm"] = confirmNewPassword
            }
            val response = userService.requestPasswordChange(requestBody)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.Success(body.getData()!!)
                }
            }

            val errorResponse = response.errorBody()?.string()!!
            return try {
                val message =
                    Gson().fromJson(errorResponse, DefaultErrorResponse::class.java).message
                Result.Error(message)
            } catch (ex: Exception) {
                return Result.Error("Error changing password ${response.code()}")
            }
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage!!)
        }
    }

    suspend fun saveTokenToServer(tokenBody: HashMap<String, String>): Result<ApiResponse<Any>> {
        return try {
            val response = vehicleService.saveToken(tokenBody)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            }else{
                Result.Error("Something went wrong")
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage!!)
        }
    }

}