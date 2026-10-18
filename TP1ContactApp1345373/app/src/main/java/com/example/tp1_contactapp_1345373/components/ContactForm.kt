package com.example.tp1_contactapp_1345373.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tp1_contactapp_1345373.R
import com.example.tp1_contactapp_1345373.data.Contact


private const val MAX_LENGTH_TEXT = 255
private const val MAX_LENGTH_PHONE = 10
private const val MAX_LENGTH_AGE = 3

/**
 * Champ texte du formulaire
 *  Limite de caractères,
 *  Compteur de caractères
 *  Astérisque si champs obligatoire
 *  Calvier adapté pour les champs
 */

@Composable
private fun ContactFormTextField(
    valueText: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isMandatory: Boolean = false,
    maxLength: Int = MAX_LENGTH_TEXT,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    OutlinedTextField(
        valueText,
        onValueChange = { typingText ->
            if (typingText.length <= maxLength) {
                onValueChange(typingText)
            }
        },
        label = {
            Row {
                Text(
                    text = labelText
                )
                if (isMandatory) {
                    Text(
                        text = "*",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        supportingText = {
            Text("${valueText.length} / $maxLength")
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
        Icon(
            imageVector = Icons.Default.Favorite,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )

    }
}

/**
 * Formulaire du contact, réutilisé pour la création ET la modification
 * Chaque changement renvoie une copie du contact au ViewModel
 */
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

        FavoriteCheckbox(
            isFavorite = contact.isFavorite,
            onFavoriteChange = { onContactSave(contact.copy(isFavorite = it)) }
        )

        ContactFormTextField(
            valueText = contact.firstName,
            onValueChange = { onContactSave(contact.copy(firstName = it)) },
            labelText = stringResource(R.string.label_firstName),
            isMandatory = true,
        )

        ContactFormTextField(
            valueText = contact.lastName,
            onValueChange = { onContactSave(contact.copy(lastName = it)) },
            labelText = stringResource(R.string.label_lastName),

        )

        ContactFormTextField(
            valueText = contact.phoneNumber,
            onValueChange = { onContactSave(contact.copy(phoneNumber = it)) },
            labelText = stringResource(R.string.label_phoneNumber),
            isMandatory = true,
            maxLength = MAX_LENGTH_PHONE,
            keyboardType = KeyboardType.Phone,

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
            maxLength = MAX_LENGTH_AGE,
            keyboardType = KeyboardType.Number

        )

        ContactFormTextField(
            valueText = contact.email,
            onValueChange = { onContactSave(contact.copy(email = it)) },
            labelText = stringResource(R.string.label_email),
            isMandatory = false,
            maxLength = MAX_LENGTH_TEXT,
            keyboardType = KeyboardType.Email,
        )

        ContactFormTextField(
            valueText = contact.address,
            onValueChange = { onContactSave(contact.copy(address = it)) },
            labelText = stringResource(R.string.label_address),
        )
    }
}