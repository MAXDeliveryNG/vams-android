package ng.max.vams.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import ng.max.vams.data.LocationRepository
import ng.max.vams.data.MovementReasonRepository
import ng.max.vams.data.RetrievalChecklistRepository
import ng.max.vams.data.VehicleTypeRepository
import ng.max.vams.data.local.AppDatabase
import ng.max.vams.data.local.dao.*
import ng.max.vams.data.remote.RemoteDataSource
import ng.max.vams.data.remote.services.UserService
import ng.max.vams.data.remote.services.VehicleService
import ng.max.vams.usecase.login.LoginUseCase
import ng.max.vams.usecase.search.SearchUseCase
import ng.max.vams.usecase.userrole.UserRoleUseCase
import ng.max.vams.usecase.vehiclelist.VehicleListUseCase
import ng.max.vams.usecase.vehiclemovement.RegisterVehicleMovementUseCase
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object DataSourceModule{

    @Provides
    fun provideRemoteDataSource(userService: UserService, vehicleService: VehicleService): RemoteDataSource {
        return RemoteDataSource(userService, vehicleService)
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
    fun providesRetrievalChecklistDao(appDatabase: AppDatabase): RetrievalChecklistDao {
        return appDatabase.retrivalChecklistDao()
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
    fun provideUserRoleUseCase(remoteDataSource: RemoteDataSource): UserRoleUseCase =
        UserRoleUseCase(remoteDataSource)

    @Provides
    fun provideMovementReasonRepository(reasonDao: ReasonDao, remoteData: RemoteDataSource): MovementReasonRepository =
        MovementReasonRepository(reasonDao, remoteData)

    @Provides
    fun provideLocationRepository(locationDao: LocationDao, remoteDataSource: RemoteDataSource, vehicleService: VehicleService): LocationRepository =
        LocationRepository(locationDao, remoteDataSource, vehicleService)


    @Provides
    fun provideVehicleTypeRepository(vehicleTypeDao: VehicleTypeDao, remoteDataSource: RemoteDataSource): VehicleTypeRepository =
        VehicleTypeRepository(vehicleTypeDao, remoteDataSource)

    @Provides
    fun provideRetrievalChecklistRepository(retrievalChecklistDao: RetrievalChecklistDao, remoteDataSource: RemoteDataSource) : RetrievalChecklistRepository =
        RetrievalChecklistRepository(retrievalChecklistDao, remoteDataSource)


}


@Module
@InstallIn(SingletonComponent::class)
object SingletonSourceModule{

    @Provides
    fun provideUserService(retrofitClient: Retrofit): UserService {
        return retrofitClient.create(UserService::class.java)
    }

    @Provides
    fun provideVehicleService(retrofitClient: Retrofit): VehicleService {
        return retrofitClient.create(VehicleService::class.java)
    }

}