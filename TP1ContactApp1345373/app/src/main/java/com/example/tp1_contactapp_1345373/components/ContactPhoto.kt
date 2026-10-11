package com.example.tp1_contactapp_1345373.components

import android.R.attr.maxWidth
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tp1_contactapp_1345373.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.tp1_contactapp_1345373.data.Contact
import androidx.core.net.toUri

@Composable
fun ContactPhoto(contact: Contact, modifier: Modifier = Modifier) {

    val contactInitials = (contact.firstName.take(1) + contact.lastName.take(1)).uppercase()


    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {

        val photoModifier = Modifier
            .fillMaxSize()
            .padding(maxWidth / 12)
            .clip((CircleShape))

        val initialsFontSize = (maxWidth.value * 0.35f).sp

        when {

            contact.photoUrl != null ->
                AsyncImage(
                    model = contact.photoUrl.toUri(),
                    contentDescription = stringResource(R.string.desc_contactPhoto),
                    contentScale = ContentScale.Crop,
                    modifier = photoModifier,
                )

            contactInitials.isNotBlank() ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = photoModifier.background(MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text = contactInitials,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = initialsFontSize,
                        fontWeight = FontWeight.Bold,
                    )
                }

            else ->
                Icon (
                   imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.desc_contactPhoto),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
        }

    }
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