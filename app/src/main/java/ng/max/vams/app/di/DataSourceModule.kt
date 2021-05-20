package ng.max.vams.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementReasonRepository
import ng.max.vams.data.VehicleTypeRepository
import ng.max.vams.data.local.AppDatabase
import ng.max.vams.data.local.dao.LocationDao
import ng.max.vams.data.local.dao.ReasonDao
import ng.max.vams.data.local.dao.VehicleDao
import ng.max.vams.data.local.dao.VehicleTypeDao
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.usecase.login.LoginUseCase
import ng.max.vams.usecase.search.SearchUseCase
import ng.max.vams.usecase.vehiclelist.VehicleListUseCase
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object DataSourceModule{

    @Provides
    fun provideUserService(retrofitClient: Retrofit): UserService {
        return retrofitClient.create(UserService::class.java)
    }

    @Provides
    fun provideVehicleService(retrofitClient: Retrofit): VehicleService {
        return retrofitClient.create(VehicleService::class.java)
    }

    @Provides
    fun provideRemoteDataSource(vehicleService: VehicleService): RemoteDataSource {
        return RemoteDataSource(vehicleService)
    }

    @Provides
    fun provideVehicleDao(appDatabase: AppDatabase): VehicleDao {
        return appDatabase.vehicleDao()
    }

    @Provides
    fun provideReasonDao(appDatabase: AppDatabase): ReasonDao {
        return appDatabase.reasonDao()
    }

    @Provides
    fun provideLocationDao(appDatabase: AppDatabase): LocationDao {
        return appDatabase.locationDao()
    }

    @Provides
    fun provideVehicleTypeDao(appDatabase: AppDatabase): VehicleTypeDao {
        return appDatabase.vehicleTypeDao()
    }

    @Provides
    fun provideLoginUseCase(userService: UserService): LoginUseCase =
        LoginUseCase(userService)

    @Provides
    fun provideVehicleListUseCase(vehicleDao: VehicleDao, remoteDataSource: RemoteDataSource): VehicleListUseCase =
            VehicleListUseCase(vehicleDao, remoteDataSource)

    @Provides
    fun provideSearchUseCase(remoteDataSource: RemoteDataSource): SearchUseCase =
        SearchUseCase(remoteDataSource)

    @Provides
    fun provideRegisterVehicleMovementUseCase(remoteDataSource: RemoteDataSource): RegisterVehicleMovementUseCase =
        RegisterVehicleMovementUseCase(remoteDataSource)

    @Provides
    fun provideMovementReasonRepository(reasonDao: ReasonDao, remoteData: RemoteDataSource): MovementReasonRepository =
        MovementReasonRepository(reasonDao, remoteData)

    @Provides
    fun provideLocationRepository(locationDao: LocationDao, remoteDataSource: RemoteDataSource): LocationRepository =
        LocationRepository(locationDao, remoteDataSource)


    @Provides
    fun provideVehicleTypeRepository(vehicleTypeDao: VehicleTypeDao, remoteDataSource: RemoteDataSource): VehicleTypeRepository =
        VehicleTypeRepository(vehicleTypeDao, remoteDataSource)





}
