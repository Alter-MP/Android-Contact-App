package com.example.tp1_contactapp_1345373.data

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

/**
 * ViewModel de l'application
 * Gestion de l'état de l'interface pour qu'il survive à la rotation, et fait le lien avec la base de données Room
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactDao =
        AppDatabase.getDatabase(application.applicationContext).contactDao()

    // Délai réutilisable
    private val stopTimeoutMillis: Long = 5000

    private val contentResolver = application.contentResolver

    /**
     * Trier en ordre alphabétique
     * Collator compare les textes selon les règles de la langue de l'appareil
     * Normalise les accents
     */
    private val collator = Collator.getInstance(Locale.getDefault())

    // Comparateur des contacts, trie par prénom et nom avec le Collator
    private val sortedValue =
        compareBy<Contact, String>(collator) { "${it.firstName} ${it.lastName}" }

    // Tous les contacts de Room, triés de A à Z
    val contacts: StateFlow<List<Contact>> = dao.getAll()
        .map { list -> list.sortedWith(sortedValue) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = emptyList()
        )

    // ÉTATS DE L'INTERFACE
    var screen by mutableStateOf(Screen.CONTACT_LIST)
    var selectedContact by mutableStateOf(Contact())
    var pendingPhotoUri by mutableStateOf<Uri?>(null)
    var isSortedAlphabeticalOrder by mutableStateOf(true)
    var showFavorites by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var darkMode by mutableStateOf<Boolean?>(null)


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
            selectedContact.phoneNumber.isBlank()

        ) {
            return ContactSaveStatus.ERROR_EMPTY_FIELDS
        }

        val contact = selectedContact.copy(
            firstName = selectedContact.firstName.trim(),
            lastName = selectedContact.lastName.trim(),
            phoneNumber = selectedContact.phoneNumber.trim(),
            email = selectedContact.email.trim(),
            address = selectedContact.address.trim(),
        )

        val result =
            if (contact.uid == 0) {

                add(contact)
                ContactSaveStatus.ADD

            } else {

                update(contact)
                ContactSaveStatus.UPDATE

            }

        completeForm(isExpanded)

        return result
    }

    /**
     * Ferme le formulaire après un enregistrement ou une suppression
     * Portrait, retour à la liste
     * Paysage, le formulaire reste visible
     */
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

    /**
     * Annule les modifications non enregistrées du formulaire
     * Portrait, retour à la liste et les modifications ne sont pas enregistrés
     * Paysage, 2 colonne reload la version enregistrée du contact
     */
    fun cancelFormChange(isExpanded: Boolean = false) {

        if (isExpanded) {

            selectedContact = contacts.value.find { it.uid == selectedContact.uid } ?: Contact()

        } else {
            returnToList()
        }
    }

    // CAMERA
    fun createImageUri(): Uri? {

        val values = ContentValues().apply {

            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        pendingPhotoUri = uri

        return uri
    }

    fun onTakePicture(success: Boolean) {

        val uri = pendingPhotoUri

        if (success && uri != null) {
            selectedContact = selectedContact.copy(photoUrl = uri.toString())
        } else if (uri != null) {
            contentResolver.delete(uri, null, null)
        }

        pendingPhotoUri = null
    }

    // GALLERY
    fun onImageGalleryPicked(uri: Uri?) {

        if (uri != null) {

            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedContact = selectedContact.copy(photoUrl = uri.toString())
        }
    }

    // CREATE
    fun add(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(contact)
    }

    // UPDATE
    fun update(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(contact)
    }

    // Change le favori d'un contact directement dans Room
    fun setFavorite(id: Int, favorite: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dao.setFavorite(id, favorite)
    }

    // DELETE
    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(contact)
    }

    // SEARCH
    // Recherche dynamique, les résultats sont triés avec le même comparateur que la liste
    fun search(query: String): Flow<List<Contact>> = dao.search(query)
        .map { list -> list.sortedWith(sortedValue) }
}