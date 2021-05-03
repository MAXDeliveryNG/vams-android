package ng.max.vams.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [DbVehicle::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun vehicleDao(): VehicleDao

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