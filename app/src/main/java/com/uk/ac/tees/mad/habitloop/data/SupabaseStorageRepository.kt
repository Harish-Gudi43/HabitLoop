package com.uk.ac.tees.mad.habitloop.data

import android.net.Uri
import com.uk.ac.tees.mad.habitloop.domain.SupabaseStorageRepository
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import java.util.UUID

class SupabaseStorageRepositoryImpl(
    private val storage: Storage
) : SupabaseStorageRepository {

    override suspend fun uploadProfilePicture(imageUri: Uri): String {
        val fileName = "${UUID.randomUUID()}"
        storage.from("satyam").upload(fileName, imageUri)
        return storage.from("satyam").publicUrl(fileName)
    }
}
