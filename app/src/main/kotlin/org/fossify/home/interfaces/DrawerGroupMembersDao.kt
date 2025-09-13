package org.fossify.home.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.fossify.home.models.DrawerGroupMember

@Dao
interface DrawerGroupMembersDao {
    @Query("SELECT * FROM drawer_group_members WHERE group_id = :groupId")
    fun getMembers(groupId: Long): List<DrawerGroupMember>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(members: List<DrawerGroupMember>)

    @Query("DELETE FROM drawer_group_members WHERE group_id = :groupId")
    fun clear(groupId: Long)
}


