package com.example.tp1_contactapp_1345373

import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.tp1_contactapp_1345373.data.Contact
import com.example.tp1_contactapp_1345373.data.ContactViewModel
import com.example.tp1_contactapp_1345373.data.Screen
import com.example.tp1_contactapp_1345373.ui.theme.TP1ContactApp1345373Theme

class MainActivity : ComponentActivity() {

    private val TAG = "CONTACT"
    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP1ContactApp1345373Theme {
                ContactApp(contactViewModel)
            }
        }
    }

    @Composable
    fun ContactApp(contactViewModel: ContactViewModel, modifier: Modifier = Modifier) {

        val context = LocalContext.current

        val contacts by contactViewModel.contacts.collectAsState()
        val screen = contactViewModel.screen
        val selectedContact = contactViewModel.selectedContact

        val fullName = "${selectedContact.firstName} ${selectedContact.lastName}"
        val messageAdded = stringResource(R.string.toast_addContact)
        val messageUpdated = stringResource(R.string.toast_updateContact)
        val messageDeleted = stringResource(R.string.toast_deleteContact)
        val messageEmptyField = stringResource(R.string.toast_emptyField_error)

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

        fun saveContact() {

            if (
                selectedContact.firstName == "" ||
                selectedContact.lastName == "" ||
                selectedContact.phoneNumber == ""
            ) {

                showToast(
                    context,
                    messageEmptyField,
                    Toast.LENGTH_LONG
                )

            } else {

                if (selectedContact.uid == 0) {
                    contactViewModel.add(selectedContact)

                    showToast(
                        context,
                        "$messageAdded : $fullName ",
                    )
                    contactViewModel.returnToList()
                } else {
                    contactViewModel.update(selectedContact)

                    showToast(
                        context,
                        "$messageUpdated : $fullName ",
                    )
                    contactViewModel.returnToList()
                }
            }
        }

        fun deleteSelectedContact() {

            contactViewModel.delete(selectedContact)

            showToast(
                context,
                "$messageDeleted : $fullName ",

                )
            contactViewModel.returnToList()
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

            when (screen) {
                Screen.CONTACT_LIST -> ContactList(
                    contacts = contacts,
                    onClick = { contactViewModel.openExistingContact(it) },
                    modifier = Modifier.padding(innerPadding)
                )

                Screen.CONTACT_FORM -> ContactForm(
                    contact = selectedContact,
                    onContactSave = { contactViewModel.selectedContact = it },
                    onCameraClick = { TODO() },
                    onGalleryClick = { TODO() },
                    modifier = Modifier.padding(innerPadding)
                )
            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ContactTopBar(
        screen: Screen,
        canDelete: Boolean,
        onAddContactClick: () -> Unit,
        onDeleteContactClick: () -> Unit,
    ) {

        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            actions = {
                when (screen) {
                    Screen.CONTACT_LIST -> IconButton(onClick = { onAddContactClick() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "AddIcon"
                        )
                    }

                    Screen.CONTACT_FORM -> if (canDelete) {
                        IconButton(onClick = { onDeleteContactClick() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "DeleteIcon"
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }

    @Composable
    fun ContactItemDetails(contact: Contact, onContactDetailsClick: () -> Unit) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContactDetailsClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ContactPhoto(modifier = Modifier.size(46.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = PhoneNumberUtils.formatNumber(contact.phoneNumber, "CA"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (contact.isFavorite) {
                Text(
                    text = stringResource(R.string.icon_favorite),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error

                )
            }
        }
    }

    @Composable
    fun ContactList(
        contacts: List<Contact>,
        onClick: (Contact) -> Unit,
        modifier: Modifier = Modifier,
    ) {

        if (contacts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.no_contacts),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(18.dp)
                )
            }

        } else {

            LazyColumn(
                modifier = modifier,
            ) {
                items(
                    contacts,
                    key = { it.uid }

                ) { contact ->

                    ContactItemDetails(
                        contact,
                        { onClick(contact) }
                    )

                    HorizontalDivider()

                }
            }
        }
    }

    @Composable
    fun ContactForm(
        contact: Contact,
        onContactSave: (Contact) -> Unit,
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll((rememberScrollState()))
                .padding(10.dp)
        ) {

            ContactPhoto(Modifier.size(124.dp))
            ContactPhotoButtons(onCameraClick = onCameraClick, onGalleryClick = onGalleryClick)

            ContactFormTextField(
                contact.firstName,
                { onContactSave(contact.copy(firstName = it.trim())) },
                stringResource(R.string.label_firstName),
            )

            ContactFormTextField(
                contact.lastName,
                { onContactSave(contact.copy(lastName = it.trim())) },
                stringResource(R.string.label_lastName),
            )

            ContactFormTextField(
                contact.phoneNumber,
                { onContactSave(contact.copy(phoneNumber = it.trim())) },
                stringResource(R.string.label_phoneNumber),
                KeyboardType.Phone,
            )

            ContactFormTextField(
                valueText = if (contact.age == null) {
                    ""
                } else {
                    contact.age.toString()
                },
                onValueChange = {
                    val newAge = if (it == "") {
                        null
                    } else {
                        it.toIntOrNull() ?: contact.age
                    }
                    onContactSave(contact.copy(age = newAge))
                },
                labelText = stringResource(R.string.label_age),
                keyboardType = KeyboardType.Number

            )

            ContactFormTextField(
                contact.email,
                { onContactSave(contact.copy(email = it.trim())) },
                stringResource(R.string.label_email),
                KeyboardType.Email,
            )

            ContactFormTextField(
                contact.address,
                { onContactSave(contact.copy(address = it.trim())) },
                stringResource(R.string.label_address),
            )

            FavoriteCheckbox(
                isFavorite = contact.isFavorite,
                onFavoriteChange = { onContactSave(contact.copy(isFavorite = it)) }
            )


        }
    }

    @Composable
    fun ContactPhoto(modifier: Modifier = Modifier) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "ContactPhoto",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier,
        )
    }

    @Composable
    fun ContactPhotoButtons(
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit,
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { onCameraClick() }) {
                Text(
                    text = stringResource(R.string.button_camera)
                )
            }

            Button(onClick = { onGalleryClick() }) {
                Text(
                    text = stringResource(R.string.button_gallery)
                )
            }
        }
    }


    @Composable
    fun ContactFormTextField(
        valueText: String,
        onValueChange: (String) -> Unit,
        labelText: String,
        keyboardType: KeyboardType = KeyboardType.Text
    ) {
        OutlinedTextField(
            value = valueText,
            onValueChange = { onValueChange(it) },
            label = {
                Text(
                    text = labelText
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
    }

    @Composable
    fun FavoriteCheckbox(isFavorite: Boolean, onFavoriteChange: (Boolean) -> Unit) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = isFavorite, onValueChange = { onFavoriteChange(it) })
                .padding(16.dp)
        ) {
            Checkbox(checked = isFavorite, onCheckedChange = null)
            Text(
                text = stringResource(R.string.favorite)
            )
        }
    }

    @Composable
    fun ContactBottomBar(
        onCancelClick: () -> Unit,
        onSaveContactClick: () -> Unit,
    ) {

        BottomAppBar(
            containerColor = Color.Transparent
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    Text(
                        text = stringResource(R.string.button_cancel),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clickable { onCancelClick() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                item {
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.height(24.dp)
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.button_save),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clickable { onSaveContactClick() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }

}

