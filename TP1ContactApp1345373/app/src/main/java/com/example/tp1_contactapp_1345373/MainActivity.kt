package com.example.tp1_contactapp_1345373

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.tp1_contactapp_1345373.components.ContactTopBar
import com.example.tp1_contactapp_1345373.data.Contact
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

            TP1ContactApp1345373Theme {
                ContactApp(contactViewModel, windowSizeClass)
            }
        }
    }

    @Composable
    fun ContactApp(
        contactViewModel: ContactViewModel,
        windowSizeClass: WindowSizeClass,
        modifier: Modifier = Modifier
    ) {

        val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        )

        val contacts by contactViewModel.contacts.collectAsState()
        val screen = contactViewModel.screen
        val selectedContact = contactViewModel.selectedContact

        val context = LocalContext.current
        val messageAdded = stringResource(R.string.toast_addContact)
        val messageUpdated = stringResource(R.string.toast_updateContact)
        val messageDeleted = stringResource(R.string.toast_deleteContact)
        val messageEmptyField = stringResource(R.string.toast_emptyField_error)

        val fullName = "${selectedContact.firstName} ${selectedContact.lastName}"

        val isContactFormVisible = isExpanded || screen == Screen.CONTACT_FORM

        fun showToast(
            context: Context,
            message: String,
            duration: Int = Toast.LENGTH_SHORT
        ) {
            Toast.makeText(
                context,
                message,
                duration
            ).show()
        }


        val takePicture = rememberLauncherForActivityResult(

            contract = ActivityResultContracts.TakePicture()

        ) { success ->

            val uri = contactViewModel.pendingUri

            if (success && uri != null) {
                contactViewModel.photoUri = uri
            } else if (uri != null) {
                context.contentResolver.delete(uri, null, null)
            }
            contactViewModel.pendingUri = null
        }

        Scaffold(
            topBar = {
                ContactTopBar(
                    screen = screen,
                    canDelete = selectedContact.uid != 0,
                    onAddContactClick = { contactViewModel.openNewContact() },
                    onDeleteContactClick = { deleteSelectedContact() }
                )
            },
            bottomBar = {
                if (screen == Screen.CONTACT_FORM) {
                    ContactBottomBar(
                        onCancelClick = { contactViewModel.returnToList() },
                        onSaveContactClick = { saveContact() }
                    )
                }
            },
            modifier = modifier
                .fillMaxSize()
                .imePadding()

        ) { innerPadding ->

            if (isExpanded) {

                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    ContactList(
                        contacts = contacts,
                        onContactClick = { contactViewModel.selectedContact = it },
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxHeight()
                    )

                    VerticalDivider()

                    ContactForm(
                        contact = selectedContact,
                        onContactSave = { contactViewModel.selectedContact = it },
                        onCameraClick = {
                            val uri = contactViewModel.createImageUri(context) ?: return@ContactForm
                            contactViewModel.pendingUri = uri

                            try {
                                takePicture.launch(uri)
                            } catch (e: ActivityNotFoundException) {
                                context.contentResolver.delete(uri, null, null)
                                contactViewModel.pendingUri = null
                            }
                        },
                        onGalleryClick = { /*TODO()*/ },
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxHeight()
                    )
                }
            } else {

                when (screen) {

                    Screen.CONTACT_LIST -> ContactList(
                        contacts = contacts,
                        onContactClick = { contactViewModel.openExistingContact(it) },
                        modifier = Modifier.padding(innerPadding)
                    )

                    Screen.CONTACT_FORM -> ContactForm(
                        contact = selectedContact,
                        onContactSave = { contactViewModel.selectedContact = it },
                        onCameraClick = { /*TODO()*/ },
                        onGalleryClick = { /*TODO()*/ },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

