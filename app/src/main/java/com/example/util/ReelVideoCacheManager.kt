package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ReelVideoCacheManager {
    private const val TAG = "ReelVideoCacheManager"
    private const val CACHE_DIR_NAME = "private_reels_cache"

    fun getCacheDir(context: Context): File {
        val dir = File(context.filesDir, CACHE_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val noMedia = File(dir, ".nomedia")
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile()
            } catch (e: Exception) {
                Log.w(TAG, "Notice creating .nomedia: ${e.message}")
            }
        }
        return dir
    }

    fun getCachedVideoFile(context: Context, reelId: Long): File? {
        val dir = getCacheDir(context)
        val file = File(dir, "reel_${reelId}.mp4")
        return if (file.exists() && file.length() > 0) file else null
    }

    fun getBundledSampleVideoUri(context: Context, reelId: Long = 1L): String {
        val rawResId = if (reelId % 2L == 0L) com.example.R.raw.sample_reel_2 else com.example.R.raw.sample_reel
        return "android.resource://${context.packageName}/$rawResId"
    }

    /**
     * Extracts the bundled sample video into private internal cache for the given reelId
     * if not already present. Returns the local file.
     */
    fun ensureLocalReelFile(context: Context, reelId: Long): File {
        val dir = getCacheDir(context)
        val file = File(dir, "reel_${reelId}.mp4")
        if (file.exists() && file.length() > 0) {
            return file
        }
        val rawResId = if (reelId % 2L == 0L) com.example.R.raw.sample_reel_2 else com.example.R.raw.sample_reel
        try {
            context.resources.openRawResource(rawResId).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Log.i(TAG, "Extracted bundled sample video for reel $reelId (${file.length()} bytes)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed extracting bundled sample video for reel $reelId: ${e.message}")
        }
        return file
    }

    fun getLocalPlaybackUri(context: Context, reelId: Long, fallbackUrl: String): String {
        // 1. Check if user already has an uploaded video or local file path
        if (fallbackUrl.startsWith("/") && File(fallbackUrl).exists() && File(fallbackUrl).length() > 0) {
            return fallbackUrl
        }
        if (fallbackUrl.startsWith("file://")) {
            val path = fallbackUrl.removePrefix("file://")
            if (File(path).exists() && File(path).length() > 0) {
                return path
            }
        }
        if (fallbackUrl.startsWith("content://") || fallbackUrl.startsWith("android.resource://")) {
            return fallbackUrl
        }

        // 2. Check if a cached video file already exists
        val cached = getCachedVideoFile(context, reelId)
        if (cached != null && cached.length() > 0) {
            return cached.absolutePath
        }

        // 3. If fallbackUrl is a known broken/restricted remote URL or blank:
        // extract and return the bundled local sample video directly!
        if (fallbackUrl.isBlank() || fallbackUrl.contains("commondatastorage.googleapis.com", ignoreCase = true)) {
            val localFile = ensureLocalReelFile(context, reelId)
            return if (localFile.exists() && localFile.length() > 0) {
                localFile.absolutePath
            } else {
                getBundledSampleVideoUri(context, reelId)
            }
        }

        // 4. For any other remote URL, ensure a local backup file is ready first so MediaPlayer
        // never crashes if the network fails or times out.
        val backupFile = ensureLocalReelFile(context, reelId)
        return if (backupFile.exists() && backupFile.length() > 0) {
            backupFile.absolutePath
        } else {
            getBundledSampleVideoUri(context, reelId)
        }
    }

    suspend fun saveUploadedVideo(context: Context, reelId: Long, sourceUri: Uri): String = withContext(Dispatchers.IO) {
        val dir = getCacheDir(context)
        val targetFile = File(dir, "reel_${reelId}.mp4")
        try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.i(TAG, "Uploaded video saved locally: ${targetFile.absolutePath}")
            targetFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy uploaded video: ${e.message}", e)
            sourceUri.toString()
        }
    }

    suspend fun cacheRemoteVideo(context: Context, reelId: Long, remoteUrl: String): File? = withContext(Dispatchers.IO) {
        if (remoteUrl.isBlank() || remoteUrl.contains("commondatastorage.googleapis.com", ignoreCase = true)) {
            return@withContext ensureLocalReelFile(context, reelId)
        }

        if (!remoteUrl.startsWith("http://", ignoreCase = true) && !remoteUrl.startsWith("https://", ignoreCase = true)) {
            val localFile = File(remoteUrl)
            return@withContext if (localFile.exists()) localFile else null
        }

        val dir = getCacheDir(context)
        val tempFile = File(dir, "temp_reel_${reelId}_${System.currentTimeMillis()}.tmp")
        val targetFile = File(dir, "reel_${reelId}.mp4")

        try {
            val url = URL(remoteUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 12000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "Mozilla/5.0 Android")
            }

            if (connection.responseCode in 200..299) {
                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.length() > 1000) {
                    if (targetFile.exists()) targetFile.delete()
                    val success = tempFile.renameTo(targetFile)
                    if (success) {
                        Log.i(TAG, "Cached video for reel $reelId (${targetFile.length()} bytes)")
                        return@withContext targetFile
                    }
                }
            }
            tempFile.delete()
            null
        } catch (e: Exception) {
            Log.w(TAG, "Caching remote reel $reelId failed: ${e.message}")
            if (tempFile.exists()) tempFile.delete()
            null
        }
    }
}
