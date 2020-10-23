package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserInfoDao {

    @Update
    fun update(userInfo: DatabaseUserInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userInfo: DatabaseUserInfo): Long

    @Query("SELECT * FROM user_info_table")
    fun getAllUsers(): LiveData<List<DatabaseUserInfo>>

    @Query("SELECT * FROM user_info_table WHERE username = :username")
    fun getUserWithUsername(username: String): LiveData<DatabaseUserInfo>

    @Query("DELETE FROM  user_info_table")
    fun clearAll()

    @Query("DELETE FROM user_info_table WHERE username = :username")
    fun clearUserWithUsername(username: String)

}