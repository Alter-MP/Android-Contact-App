package com.example.tp1_contactapp_1345373.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.text.insert

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: ContactDao =
        AppDatabase.getDatabase(application.applicationContext).contactDao()
    private val stopTimeoutMillis: Long = 5000

    // CREATE
    fun add(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(contact)
    }

    // READ

    val contacts: StateFlow<List<Contact>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = emptyList()
        )

    val favorites: StateFlow<List<Contact>> = dao.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = emptyList()
        )

    val contactSortedAscending: StateFlow<List<Contact>> = dao.getAllSortedByLastNameAscending()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = emptyList()
        )

    val contactSortedDescending: StateFlow<List<Contact>> = dao.getAllSortedByLastNameDescending()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = emptyList()
        )

    // UPDATE
    fun update(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(contact)
    }

    fun setFavorite(id: Int, favorite: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dao.setFavorite(id, favorite)
    }

    // DELETE
    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(contact)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteAll()
    }

    // SEARCH
    fun search(query: String): Flow<List<Contact>> {
        return dao.search(query)
    }
}