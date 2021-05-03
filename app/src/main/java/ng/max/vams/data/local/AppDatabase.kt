package ng.max.vams.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ng.max.vams.data.remote.response.Reason

@Database(version = 1, entities = [DbVehicle::class, Reason::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun vehicleDao(): VehicleDao
    abstract fun reasonDao(): ReasonDao

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
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}