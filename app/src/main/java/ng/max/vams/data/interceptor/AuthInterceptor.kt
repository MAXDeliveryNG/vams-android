package ng.max.vams.data.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import ng.max.vams.BuildConfig
import ng.max.vams.data.manager.UserManager
import ng.max.vams.util.notEmpty

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

        return chain.proceed(request)

    }

}