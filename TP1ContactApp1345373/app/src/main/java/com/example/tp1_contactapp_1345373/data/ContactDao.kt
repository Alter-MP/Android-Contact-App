package com.example.tp1_contactapp_1345373.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Requêtes Room sur la l'entité contacts
 * Actions CRUD et de recherche
 */
@Dao
interface ContactDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    // READ
    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<Contact>>

    // UPDATE
    @Update
    suspend fun update(contact: Contact)

    // // Change seulement la colonne isFavorite d'un contact, sans avoir UPDATE le contact au complet
    @Query("UPDATE contacts SET isFavorite = :favorite WHERE uid = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    // DELETE
    @Delete
    suspend fun delete(contact: Contact)


    /**
     * Recherche partielle, LIKE %query%, troue le query recherche n'importeou dans le champ
     * Cherche dans le prénom, le nom, le numeros de telephone, le courrielle
     */
    @Query(
        """
        SELECT * FROM contacts
        WHERE firstName LIKE '%' || :query || '%'
            OR lastName LIKE '%' || :query || '%'
            OR phoneNumber LIKE '%' || :query || '%'
            OR email LIKE '%' || :query || '%'
    """
    )
    // Return un Flow, mise à jour de la liste de contacts durant la recherche
    fun search(query: String): Flow<List<Contact>>
}