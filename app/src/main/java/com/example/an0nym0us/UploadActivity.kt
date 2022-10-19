package com.example.an0nym0us

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"

class UploadActivity : AppCompatActivity() {

    var optionMenu = arrayOf<String>("Take Photo", "Choose from gallery", "Exit")
    var mBtn: Button? = null
    var mImg: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        inizializzaBottomMenu()
        cameraGalleryFunction()
    }

    fun inizializzaBottomMenu(){
        var bottomNavigationMenuView : BottomNavigationView = findViewById(R.id.bottom_upload)
        bottomNavigationMenuView.setSelectedItemId(R.id.upload)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@UploadActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@UploadActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@UploadActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.profile -> {
                    val intent =
                        Intent(this@UploadActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }
            }
            true
        }
    }

    fun cameraGalleryFunction(){

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        photoFile = File.createTempFile(FILE_NAME, ".jpg", storageDir)

        mBtn = findViewById(R.id.buttonUpload)
        mImg = findViewById(R.id.imageUpload)

        mBtn!!.setOnClickListener {


            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
                )
            } else if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 2
                )
            } else if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA), 3
                )
            } else {
                chooseImage(this)
            }
        }
    }

    fun chooseImage(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setItems(optionMenu) { dialogInterface, i ->
            if (optionMenu[i].equals("Take Photo")) {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val fileProvider = FileProvider.getUriForFile(
                    this, "com.example.an0nym0us.fileprovider", photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                startActivityForResult(intent, 3)
            } else if (optionMenu[i].equals("Choose from gallery")) {
                var intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1)
            } else if (optionMenu[i].equals("Exit")) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCode -> {
                chooseImage(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                var uri: Uri = data.getData()!!
                var inputStream = getContentResolver()?.openInputStream(uri)
                var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                mImg!!.setImageBitmap(bitmap)
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {

            var bitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

            mImg!!.setImageBitmap(bitmap)
        }
    }
}