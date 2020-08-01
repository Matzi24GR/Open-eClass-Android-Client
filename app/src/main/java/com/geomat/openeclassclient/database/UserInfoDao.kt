package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInfo: DatabaseUserInfo)

    @Query("SELECT * FROM user_info_table")
    fun getAllUsers(): LiveData<List<DatabaseUserInfo>>

    @Query("SELECT * FROM user_info_table WHERE username = :username")
    fun getUserWithUsername(username: String): LiveData<DatabaseUserInfo>

    @Query("DELETE FROM  user_info_table")
    fun clearAll()

    @Query("DELETE FROM user_info_table WHERE username = :username")
    fun clearUserWithUsername(username: String)

}