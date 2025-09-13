package org.fossify.home.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.fossify.home.models.DrawerGroup

@Dao
interface DrawerGroupsDao {
    @Query("SELECT * FROM drawer_groups ORDER BY `order` ASC")
    fun getAll(): List<DrawerGroup>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(groups: List<DrawerGroup>)
}


