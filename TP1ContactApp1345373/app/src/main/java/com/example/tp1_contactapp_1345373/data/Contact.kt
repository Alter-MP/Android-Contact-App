package com.example.tp1_contactapp_1345373.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact (
    @PrimaryKey(autoGenerate = true)
    val uid:             Int = 0,
    val firstName:      String = "",
    val lastName:       String = "",
    val phoneNumber:    String = "",
    val age:            Int? = null,
    val email:          String = "",
    val address:        String = "",
    val isFavorite:     Boolean = false,
    val photoUrl:       String? = null,
    )