package com.example.sandanalysis

import android.app.Activity
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.jar.Manifest
import android.location.LocationManager
import android.provider.Settings
import android.location.LocationListener
import android.os.IBinder


import android.content.DialogInterface

import android.content.Context.LOCATION_SERVICE
import android.location.Location

import android.os.Looper

import android.widget.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import android.R.attr.name
import com.google.android.gms.tasks.Task

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

import android.R.attr.name
import com.google.android.gms.tasks.CancellationTokenSource


class AddRecordActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_STORAGE_CODE = 103
    private var GPS_REQUEST_CODE = 104

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
    private lateinit var fLat: TextView
    private lateinit var fLong: TextView
    private lateinit var fPlomba: TextView
    private lateinit var reloadLocation: Button

    //    User locations
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
        actionBar = supportActionBar
        actionBar!!.title = "Add Sample"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
        fLat = findViewById(R.id.fLat)
        fLong = findViewById(R.id.fLong)
        fPlomba = findViewById(R.id.fPlomba)
        reloadLocation = findViewById(R.id.reloadLocation)
        reloadLocation.setOnClickListener {
            if(checkGPSEnabled()){
                getNewLocation()
                reloadLocation.setEnabled(false)
            }else{
                getLastLocation()
            }
        }
    }

    private fun inputData() {
        inn = "" + fInn.text.toString().trim()
        val lat=fLat.text.toString()
        val long=fLong.text.toString()
        val plomba=fPlomba.text.toString()
        val timeStamp: String = (System.currentTimeMillis() + Constants.GMT5_DIFFER).toString()
        val id = dbHelper.insertSample(
            inn = "" + inn,
            image = "" + imageUri,
            plomba = plomba,
            lat = lat,
            long = long,
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
            GPS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val gpsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    Log.e("AddRecordActivity", "LOCATION ACTIVITY PERMISSION ENABLED=$gpsAccepted!")
                    if (gpsAccepted) {
                        getLastLocation()
                    } else {
                        Toast.makeText(
                            this,
                            "Location permission required!",
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
            } else if (requestCode == GPS_REQUEST_CODE) {
                getLastLocation()
            }
        }

    }


    // Location Manager helpers
    private fun getLastLocation() {
        if (checkGPSPermission()) {
            if (checkGPSEnabled()) {

                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    Log.e("AddRecordActivity", "LOCATION RESULT=${location?.latitude.toString()}")
                    if (location == null) {
                        getNewLocation()
                    } else {
                        fLat.text = location.latitude.toString()
                        fLong.text = location.longitude.toString()
                    }
                }
            } else {
                navigateToGPSSettings()
            }
        } else {
            requestGPSPermission()
        }
    }

    private fun getNewLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestGPSPermission()
            return
        } else {
            val currentLocationTask: Task<Location> =
                fusedLocationProviderClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
                )

            currentLocationTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Task completed successfully
                    val location = task.result
                    if (location != null) {
                        fLat.text = location.latitude.toString()
                        fLong.text = location.longitude.toString()
                    }

                } else {
                    Toast.makeText(this, "Xatolik yuz berdi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkGPSPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestGPSPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            GPS_REQUEST_CODE
        )
    }

    private fun checkGPSEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun navigateToGPSSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Sizda lokatsiyani aniqlash funksiyasi sozlamalarda yoqilmagan, yoqasizmi?")
            .setCancelable(false)
            .setPositiveButton(
                "Ha"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "Yo'q"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }


}
