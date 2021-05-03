package ng.max.vams.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import ng.max.vams.data.local.AppDatabase
import ng.max.vams.data.local.ReasonDao
import ng.max.vams.data.local.VehicleDao
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.usecase.assetreason.DownloadAssetReasonUseCaseImpl
import ng.max.vams.usecase.assetreason.GetAssetReasonUseCaseImpl
import ng.max.vams.usecase.login.LoginUseCaseImpl
import ng.max.vams.usecase.vehiclelist.VehicleListUseCaseImpl
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object DataSourceModule{


    @Provides
    fun provideLoginUseCaseImpl(userService: UserService): LoginUseCaseImpl =
            LoginUseCaseImpl(userService)

    @Provides
    fun provideUserService(retrofitClient: Retrofit): UserService {
        return retrofitClient.create(UserService::class.java)
    }

    @Provides
    fun provideVehicleService(retrofitClient: Retrofit): VehicleService {
        return retrofitClient.create(VehicleService::class.java)
    }

    @Provides
    fun provideVehicleDao(appDatabase: AppDatabase): VehicleDao {
        return appDatabase.vehicleDao()
    }

    @Provides
    fun provideVehicleListUseCaseImpl(vehicleDao: VehicleDao, vehicleService: VehicleService): VehicleListUseCaseImpl =
            VehicleListUseCaseImpl(vehicleDao, vehicleService)

    @Provides
    fun provideReasonDao(appDatabase: AppDatabase): ReasonDao {
        return appDatabase.reasonDao()
    }

    @Provides
    fun provideDownloadAssetReasonUseCaseImpl(reasonDao: ReasonDao, vehicleService: VehicleService): DownloadAssetReasonUseCaseImpl =
        DownloadAssetReasonUseCaseImpl(reasonDao, vehicleService)

    @Provides
    fun provideGetAssetReasonUseCaseImpl(reasonDao: ReasonDao, downloadAssetReasonUseCaseImpl: DownloadAssetReasonUseCaseImpl): GetAssetReasonUseCaseImpl =
        GetAssetReasonUseCaseImpl(reasonDao, downloadAssetReasonUseCaseImpl)



}
