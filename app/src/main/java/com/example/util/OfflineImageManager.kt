package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.util.Log
import com.example.data.entity.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Metadata model for an offline note image stored in the app's private memory.
 * These files reside strictly in internal storage (context.filesDir) and are NEVER
 * accessible or visible in the device's system gallery (Google Photos, Gallery app).
 */
data class OfflineVaultImage(
    val noteId: Long,
    val subjectName: String,
    val chapterName: String,
    val chapterNumber: Int,
    val title: String,
    val summarySnippet: String,
    val localFile: File,
    val fileSizeBytes: Long,
    val formattedSize: String,
    val lastDownloadedTimestamp: Long,
    val originalUri: String,
    val isFromCameraOrPicker: Boolean = false
)

/**
 * Status of the offline image scanning and downloading process.
 */
data class VaultSyncStatus(
    val isScanning: Boolean = false,
    val isDownloading: Boolean = false,
    val totalImagesInDb: Int = 0,
    val downloadedCount: Int = 0,
    val pendingCount: Int = 0,
    val totalVaultSizeBytes: Long = 0L,
    val formattedVaultSize: String = "0 KB",
    val currentTaskMessage: String = "All notes synced offline",
    val isOnline: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

/**
 * Manages automatic background scanning, downloading, and resolution of note images
 * into the application's private internal storage.
 *
 * Guarantees:
 * 1. Storage is strictly in `context.filesDir/private_notes_vault` (Internal Private Storage).
 * 2. Protected by `.nomedia` so Android MediaStore and System Gallery never scan or display it.
 * 3. One-time download: if already in private storage, never re-fetches from network or database.
 * 4. Automatic background download on app launch and whenever network connectivity becomes available.
 */
object OfflineImageManager {
    private const val TAG = "OfflineImageManager"
    private const val VAULT_DIR_NAME = "private_notes_vault"
    private const val LEGACY_DIR_NAME = "note_images"

    private val _syncStatus = MutableStateFlow(VaultSyncStatus())
    val syncStatus: StateFlow<VaultSyncStatus> = _syncStatus.asStateFlow()

    private val _vaultImages = MutableStateFlow<List<OfflineVaultImage>>(emptyList())
    val vaultImages: StateFlow<List<OfflineVaultImage>> = _vaultImages.asStateFlow()

    private var isNetworkMonitoringInitialized = false

    /**
     * Get or create the secure private vault directory inside context.filesDir.
     * Always ensures .nomedia file is present.
     */
    fun getVaultDirectory(context: Context): File {
        val vaultDir = File(context.filesDir, VAULT_DIR_NAME)
        if (!vaultDir.exists()) {
            vaultDir.mkdirs()
        }
        val noMediaFile = File(vaultDir, ".nomedia")
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile()
            } catch (e: Exception) {
                Log.w(TAG, "Failed creating .nomedia in vault: ${e.message}")
            }
        }
        return vaultDir
    }

    /**
     * Get or create legacy note_images directory and ensure .nomedia is there too.
     */
    fun getLegacyDirectory(context: Context): File {
        val legacyDir = File(context.filesDir, LEGACY_DIR_NAME)
        if (!legacyDir.exists()) {
            legacyDir.mkdirs()
        }
        val noMediaFile = File(legacyDir, ".nomedia")
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile()
            } catch (e: Exception) {
                // ignore
            }
        }
        return legacyDir
    }

    /**
     * Deterministic local file for a note image based on noteId and URL hash.
     */
    fun getLocalFileForRemoteUrl(context: Context, noteId: Long, remoteUrl: String): File {
        val vaultDir = getVaultDirectory(context)
        val hash = md5(remoteUrl).take(12)
        val extension = when {
            remoteUrl.contains(".png", ignoreCase = true) -> "png"
            remoteUrl.contains(".webp", ignoreCase = true) -> "webp"
            else -> "jpg"
        }
        return File(vaultDir, "vault_note_${noteId}_$hash.$extension")
    }

    /**
     * Checks if a note's image is already downloaded and present in private internal storage.
     */
    fun isImageDownloadedLocally(context: Context, noteId: Long, imageUri: String?): Boolean {
        if (imageUri.isNullOrBlank()) return false
        val uri = imageUri.trim()

        // If it's already a local file
        if (uri.startsWith("file://") || uri.startsWith("/")) {
            val localPath = uri.removePrefix("file://")
            val f = File(localPath)
            if (f.exists() && f.length() > 0) return true
        }

        // If it's a remote URL, check our deterministic vault file
        if (uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)) {
            val localFile = getLocalFileForRemoteUrl(context, noteId, uri)
            return localFile.exists() && localFile.length() > 0
        }

        return false
    }

    /**
     * Resolves the best source to pass to Coil AsyncImage.
     * If the image has been downloaded locally to private memory, returns the local File or file URI,
     * ensuring 100% offline, zero-network display!
     */
    fun resolveImageSource(context: Context, noteId: Long, imageUri: String?): Any? {
        if (imageUri.isNullOrBlank()) return null
        val uri = imageUri.trim()

        // 1. If it's already a local file path
        if (uri.startsWith("file://") || uri.startsWith("/")) {
            val localPath = uri.removePrefix("file://")
            val f = File(localPath)
            if (f.exists() && f.length() > 0) {
                return f
            }
        }

        // 2. If it's a remote URL, check if already downloaded in the private vault
        if (uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)) {
            val localFile = getLocalFileForRemoteUrl(context, noteId, uri)
            if (localFile.exists() && localFile.length() > 0) {
                return localFile
            }
            // If not downloaded yet, fallback to remote URL so it can load while download is pending
            return uri
        }

        // 3. Android Content URI
        if (uri.startsWith("content://")) {
            return Uri.parse(uri)
        }

        return uri
    }

    /**
     * Resolves the existing local private image file if available on disk.
     */
    fun getExistingLocalImageFile(context: Context, noteId: Long, imageUri: String?): File? {
        if (imageUri.isNullOrBlank()) return null
        val uri = imageUri.trim()
        if (uri.startsWith("file://") || uri.startsWith("/")) {
            val localFile = File(uri.removePrefix("file://"))
            if (localFile.exists() && localFile.length() > 0) return localFile
        }
        if (uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)) {
            val localFile = getLocalFileForRemoteUrl(context, noteId, uri)
            if (localFile.exists() && localFile.length() > 0) return localFile
        }
        val vaultDir = getVaultDirectory(context)
        val files = vaultDir.listFiles() ?: emptyArray()
        val found = files.find {
            it.name.startsWith("vault_note_${noteId}_") ||
            it.name.startsWith("note_${noteId}_") ||
            it.name.startsWith("note_${noteId}.")
        }
        if (found != null && found.length() > 0) return found
        return null
    }

    /**
     * Starts listening for network connectivity.
     * When internet becomes available, automatically runs scanAndDownloadAllNotes.
     */
    fun initNetworkMonitor(context: Context, coroutineScope: CoroutineScope, getNotes: () -> List<NoteEntity>) {
        if (isNetworkMonitoringInitialized) return
        isNetworkMonitoringInitialized = true

        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (cm != null) {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

                cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        Log.i(TAG, "Network became available! Auto-scanning database notes for offline images...")
                        _syncStatus.value = _syncStatus.value.copy(isOnline = true)
                        coroutineScope.launch {
                            val currentNotes = getNotes()
                            if (currentNotes.isNotEmpty()) {
                                scanAndDownloadAllNotes(context, currentNotes)
                            }
                        }
                    }

                    override fun onLost(network: Network) {
                        Log.d(TAG, "Network disconnected. Operating in full offline mode from private memory.")
                        _syncStatus.value = _syncStatus.value.copy(isOnline = false)
                    }
                })
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not register network callback: ${e.message}")
        }
    }

    /**
     * Scans all notes in the database.
     * Finds notes with remote images, checks if they exist in private app memory,
     * and downloads any missing images automatically in the background.
     */
    suspend fun scanAndDownloadAllNotes(
        context: Context,
        notes: List<NoteEntity>,
        onNoteUpdatedWithLocalPath: (suspend (noteId: Long, localPath: String) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val notesWithImages = notes.filter { !it.imageUri.isNullOrBlank() }
        val totalCount = notesWithImages.size

        if (totalCount == 0) {
            refreshVaultImagesList(context, notes)
            _syncStatus.value = _syncStatus.value.copy(
                isScanning = false,
                isDownloading = false,
                totalImagesInDb = 0,
                downloadedCount = 0,
                pendingCount = 0,
                currentTaskMessage = "All notes synced offline"
            )
            return@withContext
        }

        _syncStatus.value = _syncStatus.value.copy(
            isScanning = true,
            totalImagesInDb = totalCount,
            currentTaskMessage = "Scanning $totalCount note images..."
        )

        var downloadedCount = 0
        val pendingNotesToDownload = mutableListOf<Pair<NoteEntity, String>>()

        // Categorize notes
        for (note in notesWithImages) {
            val uri = note.imageUri!!.trim()
            if (uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)) {
                val localFile = getLocalFileForRemoteUrl(context, note.id, uri)
                if (localFile.exists() && localFile.length() > 0) {
                    downloadedCount++
                } else {
                    pendingNotesToDownload.add(note to uri)
                }
            } else if (uri.startsWith("file://") || uri.startsWith("/")) {
                val localFile = File(uri.removePrefix("file://"))
                if (localFile.exists() && localFile.length() > 0) {
                    downloadedCount++
                }
            } else if (uri.startsWith("content://")) {
                downloadedCount++
            }
        }

        _syncStatus.value = _syncStatus.value.copy(
            isScanning = false,
            isDownloading = pendingNotesToDownload.isNotEmpty(),
            downloadedCount = downloadedCount,
            pendingCount = pendingNotesToDownload.size,
            currentTaskMessage = if (pendingNotesToDownload.isNotEmpty())
                "Downloading ${pendingNotesToDownload.size} new note images to private memory..."
            else "All $totalCount note images downloaded in private memory"
        )

        // Download pending images
        for ((index, item) in pendingNotesToDownload.withIndex()) {
            val (note, remoteUrl) = item
            _syncStatus.value = _syncStatus.value.copy(
                currentTaskMessage = "Downloading image ${index + 1} of ${pendingNotesToDownload.size}: ${note.title.take(24)}..."
            )

            val downloadedFile = downloadSingleImage(context, note.id, remoteUrl)
            if (downloadedFile != null && downloadedFile.exists() && downloadedFile.length() > 0) {
                downloadedCount++
                _syncStatus.value = _syncStatus.value.copy(
                    downloadedCount = downloadedCount,
                    pendingCount = (pendingNotesToDownload.size - (index + 1)).coerceAtLeast(0)
                )

                val localUri = Uri.fromFile(downloadedFile).toString()
                try {
                    onNoteUpdatedWithLocalPath?.invoke(note.id, localUri)
                } catch (e: Exception) {
                    Log.w(TAG, "Notice notifying note local path: ${e.message}")
                }
            }
        }

        refreshVaultImagesList(context, notes)

        val totalSize = calculateVaultSizeBytes(context)
        _syncStatus.value = _syncStatus.value.copy(
            isScanning = false,
            isDownloading = false,
            downloadedCount = downloadedCount,
            pendingCount = 0,
            totalVaultSizeBytes = totalSize,
            formattedVaultSize = formatFileSize(totalSize),
            currentTaskMessage = "All $downloadedCount note images saved in private phone memory",
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Downloads a single image from a remote URL into the private vault directory.
     * Uses atomic rename (.tmp -> .jpg) and verification.
     */
    private fun downloadSingleImage(context: Context, noteId: Long, remoteUrl: String): File? {
        val destFile = getLocalFileForRemoteUrl(context, noteId, remoteUrl)
        if (destFile.exists() && destFile.length() > 0) {
            return destFile
        }

        val vaultDir = getVaultDirectory(context)
        val tempFile = File(vaultDir, "${destFile.name}.tmp")

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        return try {
            val url = URL(remoteUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 12000
                readTimeout = 15000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "MindLoopApp/1.0 (Android; OfflineNoteVault)")
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Failed downloading note $noteId image, response code $responseCode from $remoteUrl")
                return null
            }

            inputStream = connection.inputStream
            outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytes: Long = 0

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytes += bytesRead
            }
            outputStream.flush()
            outputStream.close()
            outputStream = null
            inputStream.close()
            inputStream = null

            if (totalBytes > 0 && tempFile.exists()) {
                if (tempFile.renameTo(destFile)) {
                    Log.i(TAG, "Successfully saved note $noteId image ($totalBytes bytes) to private storage: ${destFile.name}")
                    destFile
                } else {
                    // Fallback copy
                    tempFile.copyTo(destFile, overwrite = true)
                    tempFile.delete()
                    destFile
                }
            } else {
                tempFile.delete()
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Download error for note $noteId ($remoteUrl): ${e.message}")
            try {
                if (tempFile.exists()) tempFile.delete()
            } catch (_: Exception) {}
            null
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { inputStream?.close() } catch (_: Exception) {}
            try { connection?.disconnect() } catch (_: Exception) {}
        }
    }

    /**
     * Refreshes the reactive list of all offline vault images for display in the In-App Gallery.
     */
    fun refreshVaultImagesList(context: Context, notes: List<NoteEntity>) {
        val vaultDir = getVaultDirectory(context)
        val legacyDir = getLegacyDirectory(context)

        val noteMapById = notes.associateBy { it.id }
        val noteMapByUri = notes.associateBy { it.imageUri }

        val imageFiles = mutableListOf<File>()
        vaultDir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") && it.name.endsWith(".tmp").not() }?.let {
            imageFiles.addAll(it)
        }
        legacyDir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") && it.name.endsWith(".tmp").not() }?.let {
            imageFiles.addAll(it)
        }

        val vaultList = mutableListOf<OfflineVaultImage>()

        // 1. From downloaded files
        for (file in imageFiles) {
            if (file.length() == 0L) continue

            // Parse noteId from filename: "vault_note_{noteId}_{hash}.jpg" or "note_photo_{timestamp}.jpg"
            val extractedNoteId = if (file.name.startsWith("vault_note_")) {
                file.name.removePrefix("vault_note_").substringBefore("_").toLongOrNull()
            } else null

            val matchingNote = extractedNoteId?.let { noteMapById[it] }
                ?: notes.firstOrNull { it.imageUri?.contains(file.name) == true }
                ?: notes.firstOrNull {
                    it.imageUri?.let { u ->
                        getLocalFileForRemoteUrl(context, it.id, u).name == file.name
                    } == true
                }

            val subject = matchingNote?.subjectName ?: "Indian Polity"
            val chapter = matchingNote?.chapterName ?: "Textbook Notes"
            val chNum = matchingNote?.chapterNumber ?: 1
            val title = matchingNote?.title ?: file.name.substringBeforeLast(".")
            val summary = matchingNote?.summaryText ?: "Saved offline in private app memory."
            val original = matchingNote?.imageUri ?: Uri.fromFile(file).toString()

            vaultList.add(
                OfflineVaultImage(
                    noteId = matchingNote?.id ?: (file.lastModified() % 100000L),
                    subjectName = subject,
                    chapterName = chapter,
                    chapterNumber = chNum,
                    title = title,
                    summarySnippet = summary,
                    localFile = file,
                    fileSizeBytes = file.length(),
                    formattedSize = formatFileSize(file.length()),
                    lastDownloadedTimestamp = file.lastModified(),
                    originalUri = original,
                    isFromCameraOrPicker = file.name.startsWith("note_photo_")
                )
            )
        }

        // Sort latest first
        vaultList.sortByDescending { it.lastDownloadedTimestamp }
        _vaultImages.value = vaultList

        val totalSize = calculateVaultSizeBytes(context)
        _syncStatus.value = _syncStatus.value.copy(
            downloadedCount = vaultList.size,
            totalVaultSizeBytes = totalSize,
            formattedVaultSize = formatFileSize(totalSize)
        )
    }

    /**
     * Calculates the total storage size used in the private vault directory.
     */
    fun calculateVaultSizeBytes(context: Context): Long {
        var size = 0L
        getVaultDirectory(context).listFiles()?.forEach { if (it.isFile) size += it.length() }
        getLegacyDirectory(context).listFiles()?.forEach { if (it.isFile) size += it.length() }
        return size
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(java.util.Locale.ENGLISH, "%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> "${bytes / 1024} KB"
            else -> "$bytes B"
        }
    }

    private fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digested = md.digest(input.toByteArray())
            digested.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString().replace("-", "n")
        }
    }
}
