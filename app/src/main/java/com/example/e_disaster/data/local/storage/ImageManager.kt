package com.example.e_disaster.data.local.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val imagesDir: File = File(context.filesDir, "images")

    init {
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
    }

    /**
     * Save image from URI to local storage
     * @return Local file path or null if failed
     */
    suspend fun saveImage(uri: Uri, imageId: String): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val imageFile = File(imagesDir, "$imageId.jpg")
                FileOutputStream(imageFile).use { outputStream ->
                    stream.copyTo(outputStream)
                }
                Log.d("ImageManager", "Saved image: ${imageFile.absolutePath}")
                return@withContext imageFile.absolutePath
            }
            null
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to save image: $imageId", e)
            null
        }
    }

    /**
     * Save image from URL (download and save)
     * @return Local file path or null if failed
     */
    suspend fun saveImageFromUrl(url: String, imageId: String): String? = withContext(Dispatchers.IO) {
        try {
            val connection = java.net.URL(url).openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.getInputStream().use { inputStream ->
                val imageFile = File(imagesDir, "$imageId.jpg")
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Log.d("ImageManager", "Downloaded and saved image: ${imageFile.absolutePath}")
                return@withContext imageFile.absolutePath
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to download image: $url", e)
            null
        }
    }

    /**
     * Get local image file path
     */
    fun getImagePath(imageId: String): String? {
        val imageFile = File(imagesDir, "$imageId.jpg")
        return if (imageFile.exists()) imageFile.absolutePath else null
    }

    /**
     * Get local image URI
     */
    fun getImageUri(imageId: String): Uri? {
        val imageFile = File(imagesDir, "$imageId.jpg")
        return if (imageFile.exists()) {
            Uri.fromFile(imageFile)
        } else {
            null
        }
    }

    /**
     * Check if image exists locally
     */
    fun imageExists(imageId: String): Boolean {
        val imageFile = File(imagesDir, "$imageId.jpg")
        return imageFile.exists()
    }

    /**
     * Delete local image
     */
    suspend fun deleteImage(imageId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val imageFile = File(imagesDir, "$imageId.jpg")
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                Log.d("ImageManager", "Deleted image: $imageId - $deleted")
                deleted
            } else {
                true // Already deleted
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to delete image: $imageId", e)
            false
        }
    }

    /**
     * Get bitmap from local storage
     */
    suspend fun getBitmap(imageId: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val imageFile = File(imagesDir, "$imageId.jpg")
            if (imageFile.exists()) {
                FileInputStream(imageFile).use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to load bitmap: $imageId", e)
            null
        }
    }

    /**
     * Clear all cached images (use with caution)
     */
    suspend fun clearAllImages(): Boolean = withContext(Dispatchers.IO) {
        try {
            imagesDir.listFiles()?.forEach { it.delete() }
            Log.d("ImageManager", "Cleared all cached images")
            true
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to clear images", e)
            false
        }
    }

    /**
     * Delete image by local file path
     */
    fun deleteImageByPath(localPath: String): Boolean {
        return try {
            val file = File(localPath)
            if (file.exists()) {
                val deleted = file.delete()
                Log.d("ImageManager", "Deleted image by path: $localPath - $deleted")
                deleted
            } else {
                Log.d("ImageManager", "Image file not found: $localPath")
                true // Already deleted
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to delete image by path: $localPath", e)
            false
        }
    }

    /**
     * Delete all images for a foreign ID (e.g., victim ID)
     * This deletes images stored in subfolders like "victim/{victimId}_*.jpg"
     */
    fun deleteAllImagesForForeignId(foreignId: String, folderName: String) {
        try {
            val subDir = File(imagesDir, folderName)
            if (subDir.exists() && subDir.isDirectory) {
                subDir.listFiles { _, name -> name.startsWith("${foreignId}_") }?.forEach { file ->
                    if (file.delete()) {
                        Log.d("ImageManager", "Deleted associated image: ${file.name}")
                    } else {
                        Log.w("ImageManager", "Failed to delete associated image: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Failed to delete images for foreignId: $foreignId", e)
        }
    }
}

