package org.fossify.home.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawer_group_members")
data class DrawerGroupMember(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "activity_name") val activityName: String
)


