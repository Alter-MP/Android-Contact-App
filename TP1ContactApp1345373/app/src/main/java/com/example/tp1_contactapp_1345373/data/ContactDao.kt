package com.example.tp1_contactapp_1345373.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    // READ
    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER by lastName ASC, firstName ASC")
    fun getAllSortedByLastNameAscending(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER by lastName DESC, firstName DESC")
    fun getAllSortedByLastNameDescending(): Flow<List<Contact>>

    // UPDATE
    @Update
    suspend fun update(contact: Contact)

    @Query
        (
        """
            UPDATE contacts
            SET isFavorite = :favorite
            WHERE uid = :id
        """
    )
    suspend fun setFavorite(id: Int, favorite: Boolean)

    // DELETE
    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    // SEARCH
    @Query(
        """
        SELECT * FROM contacts
        WHERE lastName LIKE '%' || :query || '%'
            OR firstName LIKE '%' || :query || '%'
            OR phoneNumber LIKE '%' || :query || '%'
            OR email LIKE '%' || :query || '%'
        ORDER BY lastName ASC
    """
    )
    fun search(query: String): Flow<List<Contact>>

    @Query(
        """
        SELECT * FROM contacts
        WHERE isFavorite = 1
        ORDER BY lastName ASC
    """
    )
    fun getFavorites(): Flow<List<Contact>>


}