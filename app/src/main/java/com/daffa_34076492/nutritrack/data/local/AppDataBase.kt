    package com.daffa_34076492.nutritrack.data.local

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import com.daffa_34076492.nutritrack.data.model.FoodIntake
    import com.daffa_34076492.nutritrack.model.Patient


    @Database(
        entities = [Patient::class, FoodIntake::class],
        version = 7,
        exportSchema = false)

    abstract class AppDatabase : RoomDatabase() {
        /**
         * returns the [patientDao] object/.
         * returns the [foodIntakeDao] object/.
         */

        abstract fun patientDao(): PatientDao
        abstract fun foodIntakeDao(): FoodIntakeDao

        companion object {
            /**
             * Holds the database instance
             * It is volatile so that it is immediate visitable to all threads/.
             */
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "patient_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { INSTANCE = it }
                }
            }

        }
    }