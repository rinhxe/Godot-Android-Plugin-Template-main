// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.template

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import java.io.File
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    /**
     * Example showing how to declare a method that's used by Godot.
     *
     * Shows a 'Hello World' toast.
     */
    @UsedByGodot
    fun helloWorld() {
        runOnUiThread {
            //Toast for test
            //Toast.makeText(activity, "Hello World", Toast.LENGTH_LONG).show()
            //Log.v(pluginName, "Hello World")
            //Test intent
            /*val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
            activity?.startActivity(intent)*/

            saveFolder()
            openFolder()
        }
    }
    private fun saveFolder(){
        try {
            val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Tempo-Rally")
            Toast.makeText(activity, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString(), Toast.LENGTH_SHORT).show()


            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    Toast.makeText(activity, "Folder created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Failed to create folder", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Toast.makeText(activity, "Folder already exists", Toast.LENGTH_SHORT).show()
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message , Toast.LENGTH_LONG).show()

        }

    }

    private fun openFolder() {
        try {
            val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).resolve("Tempo-Rally")
            if (folder.exists()) {
                val uri = Uri.parse(folder.absolutePath)
                val intent = Intent(Intent.ACTION_VIEW)

                // Check if the device is Samsung
                if ("samsung" in Build.MANUFACTURER.lowercase()) {
                    intent.setClassName("com.sec.android.app.myfiles", "com.sec.android.app.myfiles.external.ui.MainActivity")
                    Toast.makeText(activity, "Open Samsung Files", Toast.LENGTH_SHORT).show()
                }

                intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR) // Mở thư mục bằng ứng dụng quản lý file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            } else {
                Toast.makeText(activity, "Folder does not exist", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
