package ng.max.vams.data.interceptor

import android.content.Context
import ng.max.vams.BuildConfig
import ng.max.vams.data.manager.UserManager
import ng.max.vams.util.notEmpty
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor  @Inject constructor(private val context : Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            addHeader("Content-Type", "application/json")
            addHeader(
                "Version-Info",
                context.packageName + "/Android/" + BuildConfig.VERSION_CODE
            )
            addHeader("Source", "android")

            if (UserManager.getToken().notEmpty()) {
                addHeader("Authorization", "Bearer ${UserManager.getToken()}")
            }
        }.build()

        val response: Response
        try{
            response = chain.proceed(request)
        }catch (ex: Exception){
            throw IOException(ex.localizedMessage)
        }

        return response
    }

}