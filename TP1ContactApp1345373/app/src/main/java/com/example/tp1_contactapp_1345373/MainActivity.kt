package com.example.tp1_contactapp_1345373

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass
import com.example.tp1_contactapp_1345373.components.ContactBottomBar
import com.example.tp1_contactapp_1345373.components.ContactForm
import com.example.tp1_contactapp_1345373.components.ContactList
import com.example.tp1_contactapp_1345373.components.ContactTopBar
import com.example.tp1_contactapp_1345373.data.ContactSaveStatus
import com.example.tp1_contactapp_1345373.data.ContactViewModel
import com.example.tp1_contactapp_1345373.data.Screen
import com.example.tp1_contactapp_1345373.ui.theme.TP1ContactApp1345373Theme

class MainActivity : ComponentActivity() {

    // private val TAG = "CONTACT"
    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

            TP1ContactApp1345373Theme(
                darkTheme = contactViewModel.darkMode ?: isSystemInDarkTheme()
            ) {
                ContactApp(contactViewModel, windowSizeClass)
            }
        }
    }
}

@Composable
fun ContactApp(
    contactViewModel: ContactViewModel,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {

    val contacts by contactViewModel.contacts.collectAsState()
    val screen = contactViewModel.screen
    val selectedContact = contactViewModel.selectedContact

    val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val isContactsVisible = isExpanded || screen == Screen.CONTACT_LIST
    val isContactFormVisible = isExpanded || screen == Screen.CONTACT_FORM
    val isDarkMode = contactViewModel.darkMode ?: isSystemInDarkTheme()

    val searchQuery = contactViewModel.searchQuery
    val searchResults by remember(searchQuery) {
        contactViewModel.search(searchQuery)
    }.collectAsState(initial = listOf())

    val searchedContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        searchResults
    }

    val filteredContacts = if (contactViewModel.showFavorites) {
        searchedContacts.filter { it.isFavorite }
    } else {
        searchedContacts
    }

    val displayedContacts = if (contactViewModel.isSortedAlphabeticalOrder) {
        filteredContacts
    } else {
        filteredContacts.reversed()
    }

    val context = LocalContext.current
    val messageAdded = stringResource(R.string.toast_addContact)
    val messageUpdated = stringResource(R.string.toast_updateContact)
    val messageDeleted = stringResource(R.string.toast_deleteContact)
    val messageEmptyField = stringResource(R.string.toast_emptyField_error)
    val fullName = "${selectedContact.firstName} ${selectedContact.lastName}"

    fun showToast(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, message, duration).show()
    }

    fun saveContact() {

        when (contactViewModel.saveSelectContact(isExpanded)) {

            ContactSaveStatus.ADD -> showToast("$messageAdded : $fullName")
            ContactSaveStatus.UPDATE -> showToast(messageUpdated)

            ContactSaveStatus.ERROR_EMPTY_FIELDS -> showToast(
                messageEmptyField,
                Toast.LENGTH_LONG
            )
        }
    }

    fun deleteSelectedContact() {
        contactViewModel.deleteSelectedContact(isExpanded)
        showToast( "$messageDeleted : $fullName")
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        contactViewModel.onTakePicture(success)
    }

    fun activateCamera() {

        val uri = contactViewModel.createImageUri() ?: return

        try {
            takePicture.launch(uri)

        } catch (e: ActivityNotFoundException) {
            contactViewModel.onTakePicture(false)
        }
    }

    val pickImageGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        contactViewModel.onImageGalleryPicked(uri)
    }

    fun openGalleryApp() {
        pickImageGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Scaffold(
        topBar = {
            ContactTopBar(
                showActionsButtons = isContactsVisible,
                showDeleteContactButton = isContactFormVisible && selectedContact.uid != 0,
                isFavoritesFilter = contactViewModel.showFavorites,
                isDarkMode = isDarkMode,
                onAddContactClick = { contactViewModel.openNewContact() },
                onDeleteContactClick = { deleteSelectedContact() },
                onFavoritesFilterClick = {
                    contactViewModel.showFavorites = !contactViewModel.showFavorites
                },
                onSortContactClick = { contactViewModel.isSortedAlphabeticalOrder = !contactViewModel.isSortedAlphabeticalOrder },
                onDarkModeClick = { contactViewModel.darkMode = !isDarkMode},
            )
        },
        bottomBar = {
            if (isContactFormVisible) {
                ContactBottomBar(
                    onCancelClick = { contactViewModel.cancelFormChange(isExpanded) },
                    onSaveContactClick = { saveContact() }
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .imePadding()

    ) { innerPadding ->

        when {

            isExpanded -> Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                ContactList(
                    contacts = displayedContacts,
                    searchQuery = searchQuery,
                    onContactClick = { contactViewModel.openExistingContact(it) },
                    onSearchQueryChange = { contactViewModel.searchQuery = it },
                    onFavoriteClick = { contactViewModel.setFavorite(it.uid, !it.isFavorite) },
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                )

                VerticalDivider()

                ContactForm(
                    contact = selectedContact,
                    onContactSave = { contactViewModel.selectedContact = it },
                    onCameraClick = { activateCamera() },
                    onGalleryClick = { openGalleryApp() },
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                )
            }

            screen == Screen.CONTACT_LIST -> ContactList(
                contacts = displayedContacts,
                searchQuery = searchQuery,
                onContactClick = { contactViewModel.openExistingContact(it) },
                onFavoriteClick = { contactViewModel.setFavorite(it.uid, !it.isFavorite) },
                onSearchQueryChange = { contactViewModel.searchQuery = it },
                modifier = Modifier.padding(innerPadding)
            )

            else -> ContactForm(
                contact = selectedContact,
                onContactSave = { contactViewModel.selectedContact = it },
                onCameraClick = { activateCamera() },
                onGalleryClick = { openGalleryApp() },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}




