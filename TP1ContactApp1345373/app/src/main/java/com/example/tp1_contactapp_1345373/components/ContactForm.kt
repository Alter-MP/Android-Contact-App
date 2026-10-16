package com.example.tp1_contactapp_1345373.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.tp1_contactapp_1345373.R
import com.example.tp1_contactapp_1345373.data.Contact

@Composable
private fun ContactFormTextField(
    valueText: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isMandatory: Boolean = false,
    isText: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val context = LocalContext.current

    val message = stringResource(R.string.text_limit)

    var text by remember { mutableStateOf(TextFieldValue("")) }

    var maxChar = 0

    if (isText) {
        maxChar = 25
    } else {
        maxChar = 7
    }


    OutlinedTextField(
        valueText,
        onValueChange = {
            if (it.text.lenght <= maxChar) {
                onValueChange(it)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        },
        label = {
            Row() {
                Text(
                    text = labelText
                )
                if (isMandatory) {
                    Text(
                        text = "*",
                        color = Color.Red,
                    )
                }
            }

        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        supportingText = {
            Text("${valueText.length} / $maxChar")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun FavoriteCheckbox(
    isFavorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit
) {
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

        ContactPhoto(contact, Modifier.size(124.dp))

        ContactPhotoButtons(onCameraClick = onCameraClick, onGalleryClick = onGalleryClick)

        ContactFormTextField(
            contact.firstName,
            { onContactSave(contact.copy(firstName = it)) },
            stringResource(R.string.label_firstName),
            true,
        )

        ContactFormTextField(
            contact.lastName,
            { onContactSave(contact.copy(lastName = it)) },
            stringResource(R.string.label_lastName),
            true,
        )

        ContactFormTextField(
            contact.phoneNumber,
            { onContactSave(contact.copy(phoneNumber = it)) },
            stringResource(R.string.label_phoneNumber),
            true,
            isText = false,
            KeyboardType.Phone,

            )

        ContactFormTextField(
            valueText = if (contact.age == null) {
                ""
            } else {
                contact.age.toString()
            },
            onValueChange = {
                val newAge = if (it.isBlank()) {
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
            { onContactSave(contact.copy(email = it)) },
            stringResource(R.string.label_email),
            false,
            isText = true,
            KeyboardType.Email,
        )

        ContactFormTextField(
            contact.address,
            { onContactSave(contact.copy(address = it)) },
            stringResource(R.string.label_address),
        )

        FavoriteCheckbox(
            isFavorite = contact.isFavorite,
            onFavoriteChange = { onContactSave(contact.copy(isFavorite = it)) }
        )
    }
}