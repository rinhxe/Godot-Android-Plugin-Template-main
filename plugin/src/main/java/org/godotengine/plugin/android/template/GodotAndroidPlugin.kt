// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.template
import android.app.AlertDialog
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

    @UsedByGodot
    fun showAlert(title: String, message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Okie") { dialog, _ ->
                dialog.dismiss()
            }
            // Optional: Add a negative button
            // builder.setNegativeButton("Cancel") { dialog, _ ->
            //     dialog.dismiss()
            // }
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun saveFolder(){
        try {
            val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Tempo-Rally")
//            Toast.makeText(activity, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString(), Toast.LENGTH_SHORT).show()

            if (folder.exists()) {
                return
            }

            if (!folder.mkdirs()) {
                showAlert("Cannot open Custom Levels folder", "Unable to create " + folder.absolutePath)
            }

        }catch (e : Exception){
            showAlert("Error creating Customs Level folder", e.message ?: "Unknown error")
        }

    }

    fun openFolderSamsung(folder: File) {
        try { // New samsung version
            _openFolderSamsung(folder, "com.sec.android.app.myfiles.ui.MainActivity")
        } catch (e: Exception) {
            _openFolderSamsung(folder, "com.sec.android.app.myfiles.external.ui.MainActivity")
        }
    }

    fun _openFolderSamsung(folder: File, mainActivity: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(folder.absolutePath)
        intent.setClassName("com.sec.android.app.myfiles", mainActivity)
        intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR) // Mở thư mục bằng ứng dụng quản lý file
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
        //Toast.makeText(activity, "Open Samsung Files", Toast.LENGTH_SHORT).show()
    }

    private fun openFolder() {
        try {
            val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).resolve("Tempo-Rally")
            if (!folder.exists()) {
                showAlert("Cannot open Custom Levels folder", "Unable to create " + folder.absolutePath)
                return
            }

            // Check if the device is Samsung
            if ("samsung" in Build.MANUFACTURER.lowercase()) {
                openFolderSamsung(folder)
            } else {
                val uri = Uri.parse(folder.absolutePath)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR) // Mở thư mục bằng ứng dụng quản lý file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
            }
        } catch (e: Exception) {
            showAlert("Error", e.message ?: "Unknown error")
        }
    }
}
