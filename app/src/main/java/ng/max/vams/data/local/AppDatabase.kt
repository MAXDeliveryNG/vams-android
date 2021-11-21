package ng.max.vams.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ng.max.vams.data.local.converter.Converters
import ng.max.vams.data.local.dao.*
import ng.max.vams.data.remote.response.Location
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.data.remote.response.VehicleType

@Database(version = 3, entities = [DbVehicle::class, Reason::class,
    Location::class, VehicleType::class, RetrivalChecklistItem::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun vehicleDao(): VehicleDao
    abstract fun reasonDao(): ReasonDao
    abstract fun locationDao(): LocationDao
    abstract fun vehicleTypeDao(): VehicleTypeDao
    abstract fun retrivalChecklistDao() : RetrivalChecklistDao

    companion object {
        private const val DB_NAME = "max-vams.db"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            DB_NAME
                    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .build()
                }
            }
            return INSTANCE
        }
    }
}