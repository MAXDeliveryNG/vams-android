package ng.max.vams.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val VEHICLE_TEMP = "DbVehicleNew"
const val VEHICLE = "DbVehicle"
const val REASON_TEMP = "ReasonNew"
const val REASON = "Reason"

val MIGRATION_1_2: Migration = object : Migration(1, 2){
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("CREATE TABLE IF NOT EXISTS $VEHICLE_TEMP (`championId` TEXT, " +
                "`createdAt` TEXT, `id` TEXT NOT NULL, `isMaxVehicle` INTEGER NOT NULL, " +
                "`licenseExpirationDate` TEXT, `locationId` INTEGER, `manufacturerId` INTEGER, " +
                "`maxGlobalId` TEXT, `maxVehicleId` TEXT, `modelId` INTEGER, `modelNumber` TEXT, " +
                "`plateNumber` TEXT, `pricingTemplateId` TEXT, `serviceType` TEXT, " +
                "`updatedAt` TEXT, `movementType` TEXT, `vehicleStatusId` INTEGER, " +
                "`vehicleTypeId` INTEGER, `year` INTEGER, `lastVehicleMovement` TEXT, " +
                "`champion` TEXT, `status` TEXT, PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS $REASON_TEMP (`createdAt` TEXT NOT NULL, " +
                "`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `slug` TEXT NOT NULL, " +
                "`subReasons` TEXT, PRIMARY KEY(`id`))")

        database.execSQL("INSERT INTO $VEHICLE_TEMP (championId, createdAt, id, isMaxVehicle, " +
                "licenseExpirationDate, locationId, manufacturerId, maxGlobalId, maxVehicleId, " +
                "modelId, modelNumber, plateNumber, pricingTemplateId, serviceType, updatedAt, " +
                "movementType, vehicleStatusId, vehicleTypeId, year, lastVehicleMovement) " +
                "SELECT * FROM $VEHICLE")



        // Remove the old table
        database.execSQL("DROP TABLE $VEHICLE")
        database.execSQL("DROP TABLE $REASON")

        // Change the table name to the correct one
        database.execSQL("ALTER TABLE $VEHICLE_TEMP RENAME TO $VEHICLE")
        database.execSQL("ALTER TABLE $REASON_TEMP RENAME TO $REASON")
    }
}