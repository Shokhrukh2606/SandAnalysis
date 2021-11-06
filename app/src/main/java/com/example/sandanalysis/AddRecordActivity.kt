package com.example.sandanalysis

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.jar.Manifest

class AddRecordActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_STORAGE_CODE = 103

    //    action bar
    private var actionBar: ActionBar? = null

    //    database variable
    private lateinit var dbHelper: DatabaseHelper

    //    arrays
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    private lateinit var fInn: EditText
    private var inn: String? = ""
    private lateinit var sampleImage: ImageView
    private lateinit var addRecordButton: Button
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
        actionBar = supportActionBar
        actionBar!!.title = "Add Sample"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        sampleImage = findViewById(R.id.sample_image)
        sampleImage.setOnClickListener {
            imagePickDialog()
        }
        addRecordButton = findViewById(R.id.addButton)
        addRecordButton.setOnClickListener {
            inputData()
        }
        fInn = findViewById(R.id.inn)
    }

    private fun inputData() {
        inn = "" + fInn.text.toString().trim()
        val timeStamp = System.currentTimeMillis()
        val id = dbHelper.insertSample(
            inn = "" + inn,
            image = "" + imageUri,
            lat = (12).toFloat(),
            long = (12).toFloat(),
            addTimeStamp = "" + timeStamp,
            updatedTimeStamp = "" + timeStamp
        )
        Toast.makeText(
            this,
            "Sample added successfully!",
            Toast.LENGTH_LONG
        ).show()

    }

    private fun imagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick image from")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    private fun pickFromGallery() {
        val gallerryIntent = Intent(Intent.ACTION_PICK)
        gallerryIntent.type = "image/*"
        startActivityForResult(
            gallerryIntent,
            IMAGE_PICK_STORAGE_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val resultC = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val resultS = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return resultC && resultS
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                Log.e("AddRecordActivity", "grantResults " + grantResults.toString());
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Camera and Storage permission required!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Storage permission required!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_STORAGE_CODE) {
                CropImage.activity(data!!.data).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this)
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageUri = resultUri
                    sampleImage.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Toast.makeText(
                        this,
                        "" + error,
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }

    }
}