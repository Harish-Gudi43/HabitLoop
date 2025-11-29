package com.uk.ac.tees.mad.habitloop.domain

import android.net.Uri

interface SupabaseStorageRepository {
    suspend fun uploadProfilePicture(imageUri: Uri): String
}
