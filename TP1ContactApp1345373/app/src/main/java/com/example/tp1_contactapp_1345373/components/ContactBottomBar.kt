package com.example.tp1_contactapp_1345373.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tp1_contactapp_1345373.R

/**
 * BottomBar : afficher seulement si formulaire est visible
 *  Boutons Annuler et Enregistrer
 */
@Composable
fun ContactBottomBar(
    onCancelClick: () -> Unit,
    onSaveContactClick: () -> Unit,
) {

    BottomAppBar(
        containerColor = Color.Transparent,
        contentPadding = PaddingValues(0.dp),
        windowInsets = WindowInsets(0),
        modifier = Modifier.navigationBarsPadding().height(56.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = stringResource(R.string.button_cancel),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable { onCancelClick() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )

            VerticalDivider(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.height(24.dp)
            )

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