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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp1_contactapp_1345373.R
import com.example.tp1_contactapp_1345373.data.Contact


@Composable
private fun ContactItemDetails(contact: Contact, onContactDetailsClick: () -> Unit) {

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

            Text(
                text = PhoneNumberUtils.formatNumber(
                    contact.phoneNumber, locale.platformLocale.country
                ),
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
    onContactClick: (Contact) -> Unit,
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
                ) { contact ->

                ContactItemDetails(
                    contact,
                    { onContactClick(contact) }
                )

                HorizontalDivider()
            }
        }
    }
}


