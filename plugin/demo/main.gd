extends Node2D

var _plugin_name = "GodotAndroidPluginTemplate"
var _android_plugin

func _ready():
	if Engine.has_singleton(_plugin_name):
		_android_plugin = Engine.get_singleton(_plugin_name)
		_android_plugin.connect("file_selected", Callable(self, "_on_file_selected"))
		_android_plugin.connect("file_selection_canceled", Callable(self, "_on_file_canceled"))
	else:
		printerr("Couldn't find plugin " + _plugin_name)

func _on_Button_pressed():
	if _android_plugin:
		_android_plugin.helloWorld()
		
func _on_pick_file_press():
	if _android_plugin:
		_android_plugin.pickUpFile()

func _on_file_selected(file_path: String, file_name: String):
	print("File received:", file_path)
	print("Original file name:", file_name)
	
	$Label.text = "Selected: " + file_name
	
	var new_file_path = "user://saved_files/" + file_name
	
	DirAccess.make_dir_recursive_absolute("user://saved_files")
	
	if FileAccess.file_exists(file_path):
		if DirAccess.copy_absolute(file_path, new_file_path) == OK:
			print("File saved as:", new_file_path)
			_process_file(new_file_path)
		else:
			print("Failed to copy file")
			$Label2.text = "Failed to copy file"
	else:
		print("File does not exist")
		$Label2.text = "File does not exist"

func _process_file(file_path: String):
	if not FileAccess.file_exists(file_path):
		print("File does not exist:", file_path)
		$Label2.text = "Error: File does not exist!"
		return
	
	var extension = file_path.get_extension().to_lower()
	
	print("Processing file:", file_path)
	print("File extension:", extension)
	$Label2.text = "Processing file: " + file_path + "\nExtension: " + extension
	
	if extension in ["txt", "json"]:
		var file = FileAccess.open(file_path, FileAccess.READ)
		if file:
			var content = file.get_as_text()
			print("File content:", content)
			$Label2.text += "\nFile content:\n" + content
		else:
			print("Failed to open file")
			$Label2.text += "\nFailed to open file"
	else:
		$Label2.text += "\nUnsupported file type"
