package ng.max.vams.app.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ng.max.vams.BuildConfig
import ng.max.vams.data.interceptor.AuthInterceptor
import ng.max.vams.data.local.AppDatabase
import ng.max.vams.data.manager.SharedPrefsManager
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceComponentModule {

    //Local Module
    @Provides
    @Singleton
    fun provideSharedPrefsManager(@ApplicationContext appContext: Context): SharedPrefsManager {
        return SharedPrefsManager(appContext)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getInstance(appContext)!!
    }

    //Remote module
    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL).build()
    }

    @Suppress("ConstantConditionIf")
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, chuckInterceptor: ChuckerInterceptor): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)

        val spec: ConnectionSpec = if (BuildConfig.IS_DEV) {
            ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
        } else {
            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(
                    TlsVersion.SSL_3_0,
                    TlsVersion.TLS_1_0,
                    TlsVersion.TLS_1_1,
                    TlsVersion.TLS_1_2
                )
                .build()
        }

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(chuckInterceptor)
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
        }

        okHttpClientBuilder.addInterceptor(authInterceptor)
        return okHttpClientBuilder
            .connectionSpecs(mutableListOf(spec))
            .build()
    }

    @Provides
    fun provideAuthInterceptor(@ApplicationContext appContext: Context): AuthInterceptor {
        return AuthInterceptor(appContext)
    }

    @Provides
    fun provideChuckerInterceptor(@ApplicationContext appContext: Context): ChuckerInterceptor {
        // Create the Collector
        val chuckerCollector = ChuckerCollector(
            context = appContext,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(appContext)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .redactHeaders("Auth-Token", "Bearer")
            .alwaysReadResponseBody(true)
            .build()
    }
}

