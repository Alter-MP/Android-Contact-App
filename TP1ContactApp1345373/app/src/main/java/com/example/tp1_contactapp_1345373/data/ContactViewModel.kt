package com.example.tp1_contactapp_1345373.data

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactDao =
        AppDatabase.getDatabase(application.applicationContext).contactDao()

    private val stopTimeoutMillis: Long = 5000

    var screen by mutableStateOf(Screen.CONTACT_LIST)
    var selectedContact by mutableStateOf(Contact())

    // NAVIGATION
    fun openExistingContact(contact: Contact) {
        selectedContact = contact
        screen = Screen.CONTACT_FORM
    }

    fun openNewContact() {
        openExistingContact(Contact())
    }

    fun returnToList() {
        screen = Screen.CONTACT_LIST
    }

    // FORM
    fun saveSelectContact(isExpanded: Boolean = false): ContactSaveStatus {

        if (

            selectedContact.firstName.isBlank() ||
            selectedContact.lastName.isBlank() ||
            selectedContact.phoneNumber.isBlank()

        ) {
            return ContactSaveStatus.ERROR_EMPTY_FIELDS
        }

        val existingContact = contacts.value.find { it.uid == selectedContact.uid }

        val result =
            if (existingContact == null) {

                add(selectedContact)
                ContactSaveStatus.ADD

            } else {

                update(selectedContact)
                ContactSaveStatus.UPDATE

            }

        completeForm(isExpanded)

        return result
    }

    private fun completeForm(isExpanded: Boolean) {

        returnToList()

        if (isExpanded) {
            selectedContact = Contact()
        }
    }

    fun deleteSelectedContact(isExpanded: Boolean = false) {

        delete(selectedContact)

        completeForm(isExpanded)
    }

    fun cancelFormChange(isExpanded: Boolean = false) {

        if (!isExpanded) {
            returnToList()

        } else {
            selectedContact = contacts.value.find { it.uid == selectedContact.uid } ?: Contact()
        }
    }

    // CAMERA
    var pendingUri by mutableStateOf<Uri?>(null)
    var photoUri by mutableStateOf<Uri?>(null)

    fun createImageUri(context: Context): Uri? {

        val values = ContentValues().apply {

            put(MediaStore.Images.Media.MIME_TYPE, "/image/jpeg")
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
    }

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

    val contactSortedDescending: StateFlow<List<Contact>> = dao.getAllSortedDescending()
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