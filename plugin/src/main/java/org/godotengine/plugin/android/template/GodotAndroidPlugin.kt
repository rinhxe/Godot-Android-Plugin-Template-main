// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.template
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Environment
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import java.io.File
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.widget.Toast
import org.godotengine.godot.plugin.SignalInfo
import java.io.FileOutputStream
import java.io.InputStream

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    /**
     * Example showing how to declare a method that's used by Godot.
     *
     * Shows a 'Hello World' toast.
     */
    //region OpenFolder
    @UsedByGodot
    fun helloWorld() {
        runOnUiThread {
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
    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 1001
    }
    //endregion
    //region PickUpFile
    @UsedByGodot
    fun pickUpFile() {
        activity?.runOnUiThread {
            Toast.makeText(activity, "You are picking a file", Toast.LENGTH_LONG).show()

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            activity?.startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }
    }

    override fun onMainActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data?.data != null) {
                val uri = data.data!!
                val fileName = getFileName(uri)
                val file = saveFileToInternalStorage(uri, fileName)

                if (file != null) {
                    emitSignal("file_selected", file.absolutePath, fileName)
                    Toast.makeText(activity, "Emit file selected", Toast.LENGTH_LONG).show()
                } else {
                    emitSignal("file_selection_canceled")
                    Toast.makeText(activity, "Emit file not selected", Toast.LENGTH_LONG).show()
                }
            } else {
                emitSignal("file_selection_canceled")
                Toast.makeText(activity, "Emit file not selected", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveFileToInternalStorage(uri: Uri, fileName: String): File? {
        return try {
            val inputStream: InputStream? = activity?.contentResolver?.openInputStream(uri)
            val file = File(activity?.filesDir, fileName) // Lưu file với đúng tên gốc
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown_file"
        activity?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex("_display_name")
                if (index != -1) {
                    name = cursor.getString(index)
                }
            }
        }
        return name
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo("file_selected", String::class.java, String::class.java), // Trả về cả đường dẫn và tên file
            SignalInfo("file_selection_canceled")
        )
    }
//endregion


}
