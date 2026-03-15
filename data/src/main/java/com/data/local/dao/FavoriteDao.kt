package com.data.local.dao

import androidx.room.*
import com.data.local.entity.FavoriteShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_shows ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(show: FavoriteShowEntity)

    @Query("DELETE FROM favorite_shows WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shows WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT id FROM favorite_shows")
    suspend fun getAllFavoriteIds(): List<Int>
}
