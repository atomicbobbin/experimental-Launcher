package org.fossify.home.databases

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.fossify.home.helpers.Converters
import org.fossify.home.interfaces.AppLaunchersDao
import org.fossify.home.interfaces.HiddenIconsDao
import org.fossify.home.interfaces.DrawerGroupsDao
import org.fossify.home.interfaces.DrawerGroupMembersDao
import org.fossify.home.interfaces.HomeScreenGridItemsDao
import org.fossify.home.models.AppLauncher
import org.fossify.home.models.HiddenIcon
import org.fossify.home.models.HomeScreenGridItem
import org.fossify.home.models.DrawerGroup
import org.fossify.home.models.DrawerGroupMember

@Database(
    entities = [AppLauncher::class, HomeScreenGridItem::class, HiddenIcon::class, DrawerGroup::class, DrawerGroupMember::class],
    version = 7
)
@TypeConverters(Converters::class)
abstract class AppsDatabase : RoomDatabase() {

    abstract fun AppLaunchersDao(): AppLaunchersDao

    abstract fun HomeScreenGridItemsDao(): HomeScreenGridItemsDao

    abstract fun HiddenIconsDao(): HiddenIconsDao

    abstract fun DrawerGroupsDao(): DrawerGroupsDao

    abstract fun DrawerGroupMembersDao(): DrawerGroupMembersDao

    companion object {
        private var db: AppsDatabase? = null

        fun getInstance(context: Context): AppsDatabase {
            if (db == null) {
                synchronized(AppsDatabase::class) {
                    if (db == null) {
                        val migration5to6 = object : Migration(5, 6) {
                            override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL(
                                    "CREATE TABLE IF NOT EXISTS `drawer_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `order` INTEGER NOT NULL)"
                                )
                            }
                        }

                        val migration6to7 = object : Migration(6, 7) {
                            override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL(
                                    "CREATE TABLE IF NOT EXISTS `drawer_group_members` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `group_id` INTEGER NOT NULL, `package_name` TEXT NOT NULL, `activity_name` TEXT NOT NULL)"
                                )
                            }
                        }

                        db = Room.databaseBuilder(
                            context.applicationContext,
                            AppsDatabase::class.java,
                            "apps.db"
                        ).addMigrations(migration5to6, migration6to7).build()
                    }
                }
            }
            return db!!
        }
    }
}
