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
import com.example.tp1_contactapp_1345373.data.Contact
import com.example.tp1_contactapp_1345373.data.ContactSaveStatus
import com.example.tp1_contactapp_1345373.data.ContactViewModel
import com.example.tp1_contactapp_1345373.data.Screen
import com.example.tp1_contactapp_1345373.ui.theme.TP1ContactApp1345373Theme

class MainActivity : ComponentActivity() {
    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            /**
             * Classe de taille de la fenêtre, recalculée à chaque rotation
             * Détermine l'affichage en une colonne, portrait, et deux colonnes, paysage
             */
            val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

            // Thème de l'application, choix de l'utilisateur ou thème du système
            TP1ContactApp1345373Theme(
                darkTheme = contactViewModel.darkMode ?: isSystemInDarkTheme()
            ) {
                ContactApp(contactViewModel, windowSizeClass)
            }
        }
    }
}

/**
    Écran principal de l'application
    Mode compact portrait / vertical :
        affiche la liste de contact OU le formulaire de contact selon Screen

    Mode étendu paysage/ horizontal :
        affiche la liste de contact et le formulaire contact côte à côte

    Gestion:
        Toast dynamique selon l'intéraction de l'utilisateur
        Ouverture de la caméra
        Accès à la galerie de photos
 */
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
        widthDpBreakpoint = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )

    val isContactsVisible = isExpanded || screen == Screen.CONTACT_LIST

    val isContactFormVisible = isExpanded || screen == Screen.CONTACT_FORM

    val isDarkMode = contactViewModel.darkMode ?: isSystemInDarkTheme()

    // Texte entré dans la barre de recherche, gardé dans le ViewModel pour survivre à la rotation
    val searchQuery = contactViewModel.searchQuery

    /**
     *  remember(searchQuery) :
     *      Une nouvelle requête Room est créée seulement quand le texte change
     *
     *  ollectAsState transforme le Flow en état Compose:
     *      L'écran refresh la liste quand les nouveaux résultats arrivent
     */
    val searchResults by remember(searchQuery) {
        contactViewModel.search(searchQuery)
    }.collectAsState(initial = listOf())

    /**
     * Affichage de tous les contacts lorsque que la barre de recherche est vide
     * S'il y a une recherche, afffiché seulement les résultats
     */
    val searchedContacts = if (searchQuery.isBlank()) {
        contacts
    } else {
        searchResults
    }

    // Bouton favoris de la TopBar actif, on affiche seulement les contacts favoris
    val filteredContacts = if (contactViewModel.showFavorites) {
        searchedContacts.filter { it.isFavorite }
    } else {
        searchedContacts
    }

    // Triage ASC ou DESC de la liste de contacts afficher, filter, rechercher
    val displayedContacts = if (contactViewModel.isSortedAlphabeticalOrder) {
        filteredContacts
    } else {
        filteredContacts.reversed()
    }

    val context = LocalContext.current
    val fullName = "${selectedContact.firstName} ${selectedContact.lastName}".trim()
    val messageAdded = stringResource(R.string.toast_addContact, fullName)
    val messageUpdated = stringResource(R.string.toast_updateContact, fullName)
    val messageDeleted = stringResource(R.string.toast_deleteContact, fullName)
    val messageEmptyField = stringResource(R.string.toast_emptyField_error)

    fun showToast(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, message, duration).show()
    }

    fun saveContact() {

        when (contactViewModel.saveSelectContact(isExpanded)) {

            ContactSaveStatus.ADD -> showToast(messageAdded)

            ContactSaveStatus.UPDATE -> showToast(messageUpdated)

            ContactSaveStatus.ERROR_EMPTY_FIELDS -> showToast(
                messageEmptyField, duration = Toast.LENGTH_LONG
            )
        }
    }

    fun deleteSelectedContact() {
        contactViewModel.deleteSelectedContact(isExpanded)
        showToast(messageDeleted)
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        contactViewModel.onTakePicture(success)
    }

    fun activateCamera() {

        val uri = contactViewModel.createImageUri() ?: return

        try {
            takePicture.launch(input = uri)

        } catch (_: ActivityNotFoundException) {
            contactViewModel.onTakePicture(success = false)
        }
    }

    val pickImageGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        contactViewModel.onImageGalleryPicked(uri)
    }

    fun openGalleryApp() {
        pickImageGallery.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * TopBar : Affiche le titre et des boutons d'actions
     *      bouton Supprimer s'affiche seulement pour un contact déjà enregistré
     *
     * BottomBar : Affiche les boutons d'action du form
     *      s'affiche seulement quand le formulaire est visible
     *
     * imePadding : le contenu est au-dessus le clavier
     */
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
                onSortContactClick = {
                    contactViewModel.isSortedAlphabeticalOrder =
                        !contactViewModel.isSortedAlphabeticalOrder
                },
                onDarkModeClick = { contactViewModel.darkMode = !isDarkMode },
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
                    .padding(paddingValues = innerPadding)
                    .fillMaxSize()
            ) {
                ContactListPanel(
                    contactViewModel = contactViewModel,
                    contacts = displayedContacts,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                )

                VerticalDivider()

                ContactFormPanel(
                    contactViewModel = contactViewModel,
                    onCameraClick = { activateCamera() },
                    onGalleryClick = { openGalleryApp() },
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                )
            }

            screen == Screen.CONTACT_LIST -> ContactListPanel(
                contactViewModel = contactViewModel,
                contacts = displayedContacts,
                modifier = Modifier.padding(paddingValues = innerPadding)
            )

            else -> ContactFormPanel(
                contactViewModel = contactViewModel,
                onCameraClick = { activateCamera() },
                onGalleryClick = { openGalleryApp() },
                modifier = Modifier.padding(paddingValues = innerPadding)
            )
        }
    }
}

/**
 * Relie le composant ContactList au ViewModel
 * Utilisé en portrait et en mode deux colonnes
 */
@Composable
private fun ContactListPanel(
    contactViewModel: ContactViewModel,
    contacts: List<Contact>,
    modifier: Modifier = Modifier
) {
    ContactList(
        contacts = contacts,
        searchQuery = contactViewModel.searchQuery,
        onContactClick = { contactViewModel.openExistingContact(it) },
        onFavoriteClick = { contactViewModel.setFavorite(it.uid, !it.isFavorite) },
        onSearchQueryChange = { contactViewModel.searchQuery = it },
        modifier = modifier
    )
}

/**
 * Relie le composant ContactForm au ViewModel.
 * Utilisé en portrait et en mode deux colonnes
 */
@Composable
private fun ContactFormPanel(
    contactViewModel: ContactViewModel,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContactForm(
        contact = contactViewModel.selectedContact,
        onContactSave = { contactViewModel.selectedContact = it },
        onCameraClick = onCameraClick,
        onGalleryClick = onGalleryClick,
        modifier = modifier
    )
}
