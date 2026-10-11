package com.example.tp1_contactapp_1345373.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tp1_contactapp_1345373.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactTopBar(
    showAddContactButton: Boolean,
    showDeleteContactButton: Boolean,
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
            if (showAddContactButton) {
                IconButton(onClick = { onAddContactClick() }) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.icon_addContact)
                    )
                }
            }

            if (showDeleteContactButton) {

                IconButton(onClick = { onDeleteContactClick() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.icon_deleteContact)
                    )
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