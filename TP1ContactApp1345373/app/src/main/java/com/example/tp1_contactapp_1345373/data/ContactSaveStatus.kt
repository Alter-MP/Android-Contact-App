package com.example.tp1_contactapp_1345373.data

/**
 * Résultat d'un enregistrement de contact qui détermine l'affichafe du Toast
 *
 */
enum class ContactSaveStatus {
    ADD,
    UPDATE,
    ERROR_EMPTY_FIELDS,
}