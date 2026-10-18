package com.example.tp1_contactapp_1345373.components

import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tp1_contactapp_1345373.R
import com.example.tp1_contactapp_1345373.data.Contact
import java.text.Normalizer

/**
 * Ligne d'un contact de la lsite
 *  Photo
 *  Prénom + Nom,
 *  Téléphone formaté
 *  IconButton étoile de favori
 */

@Composable
private fun ContactItemDetails(
    contact: Contact,
    onContactDetailsClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {

    val locale = LocalLocale.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onContactDetailsClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ContactPhoto(contact, modifier = Modifier.size(46.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${contact.firstName} ${contact.lastName}",
                style = MaterialTheme.typography.titleMedium
            )

            // Formate le numéro selon le pays de l'appareil
            Text(
                text = PhoneNumberUtils.formatNumber(
                    contact.phoneNumber, locale.platformLocale.country
                ) ?: contact.phoneNumber
                ,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Permet de rendre un contact favori à partir de la liste, sans rentrer dans le formulaire
        IconButton(
            onClick = { onFavoriteClick() }
        ) {
            Icon(
                imageVector =
                    if (contact.isFavorite) {
                        Icons.Default.Star
                    } else {
                        Icons.Default.StarBorder
                    },
                contentDescription = stringResource(R.string.favorite),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}


/**
 * Retourne la lettre d'index du contact ou "#" si ce n'est pas une lettre.
 */

private fun indexContacts(contact: Contact): String {

    // Normaliser les accents pour l'indexation alphabétique de la liste de contacts
    val letter =
        Normalizer.normalize(
            contact.firstName.take(1), Normalizer.Form.NFD
        ).take(1).uppercase()

    val rangeStart = "A"
    val rangeEnd = "Z"
    val numberIndex = "#"

    return if (letter in rangeStart..rangeEnd) {
        letter
    } else {
        numberIndex
    }
}

/**
 * En-tête de section avec # et l'alphabets
 */
@Composable
private fun LetterHeaderFormat(letter: String) {
    Text(
        text = letter,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

/**
 * Barre de recherche
 * Liste des contacts groupés par lettre
 */
@Composable
fun ContactList(
    contacts: List<Contact>,
    searchQuery: String,
    onContactClick: (Contact) -> Unit,
    onFavoriteClick: (Contact) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier.fillMaxSize(),
    ) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchQueryChange(it) },
            label = {
                Text(
                    text = stringResource(R.string.label_search)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.icon_search)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        )

        if (contacts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.no_search_results),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(18.dp)
                )
            }

        } else {

            val sortedContactsAlphabetical = contacts.groupBy { indexContacts(it) }

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {

                sortedContactsAlphabetical.forEach { (letter, contactsIndexLetter) ->

                    // Clé unique pour chaque en-tête et chaque contact : garde la position du défilement
                    item(key = "index_$letter") {
                        LetterHeaderFormat(letter)
                    }

                    items(
                        contactsIndexLetter,
                        key = { it.uid }
                    ) { contact ->

                        ContactItemDetails(
                            contact,
                            onContactDetailsClick = { onContactClick(contact) },
                            onFavoriteClick = { onFavoriteClick(contact) }
                        )

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}


