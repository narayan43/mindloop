package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageHelper {
    private const val TAG = "ImageStorageHelper"

    /**
     * Safely copies an image picked via Android PhotoPicker or Camera to private app internal storage.
     * Guaranteed to be kept strictly inside context.filesDir with .nomedia protection,
     * so it NEVER appears in the Android system gallery or Google Photos.
     * Returns the persistent absolute file URI string for 100% offline usage.
     */
    fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String {
        return try {
            val vaultDir = OfflineImageManager.getVaultDirectory(context)
            val fileName = "note_photo_${System.currentTimeMillis()}.jpg"
            val destFile = File(vaultDir, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            if (inputStream != null) {
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
                Log.d(TAG, "Successfully saved note photo to private app memory: ${destFile.absolutePath}")
                Uri.fromFile(destFile).toString()
            } else {
                sourceUri.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy image to internal storage: ${e.message}", e)
            sourceUri.toString()
        }
    }

    fun saveMultipleImagesToInternalStorage(context: Context, uris: List<Uri>): String {
        val savedUris = uris.mapNotNull { uri ->
            try {
                saveImageToInternalStorage(context, uri)
            } catch (e: Exception) {
                Log.e(TAG, "Failed saving one image in multi-batch: ${e.message}")
                null
            }
        }
        return savedUris.joinToString(";;;")
    }

    /**
     * Parses a stored image URI string which may contain one or multiple image paths/URLs
     * separated by delimiter ';;;' or newline.
     */
    fun parseImageUris(rawUriString: String?): List<String> {
        if (rawUriString.isNullOrBlank()) return emptyList()
        return rawUriString.split(";;;", "\n", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    /**
     * Delegates to OfflineImageManager to resolve local private file vs remote fallback for Coil
     */
    fun resolveImageSource(context: Context, noteId: Long, imageUri: String?): Any? {
        return OfflineImageManager.resolveImageSource(context, noteId, imageUri)
    }
}
