package com.example.tp1_contactapp_1345373.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SortByAlpha
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

/**
 * TopBar : titre de l'application et boutons d'actions
 *  mode sombre, filtre des favoris, tri et ajout.
 *  bouton de suppresion si contact existant
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactTopBar(
    showActionsButtons: Boolean,
    showDeleteContactButton: Boolean,
    isFavoritesFilter: Boolean,
    isDarkMode: Boolean,
    onAddContactClick: () -> Unit,
    onSortContactClick: () -> Unit,
    onFavoritesFilterClick: () -> Unit,
    onDeleteContactClick: () -> Unit,
    onDarkModeClick: () -> Unit,
) {

    TopAppBar(

        title = {
            Text(
                text = stringResource(R.string.title),
                style = MaterialTheme.typography.titleLarge
            )
        },

        actions = {

            IconButton(onClick = { onDarkModeClick() }) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = stringResource(R.string.settings_darkMode)
                )
            }

            if (showActionsButtons) {

                IconButton(onClick = { onFavoritesFilterClick() }) {
                    Icon(
                        imageVector = if (isFavoritesFilter) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = stringResource(R.string.icon_favoritesFilter)
                    )
                }

                IconButton(
                    onClick = { onSortContactClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = stringResource(R.string.icon_sort)
                    )
                }

                IconButton(
                    onClick = { onAddContactClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
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
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}