package com.example.an0nym0us

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"

class UploadActivity : AppCompatActivity() {

    var optionMenu = arrayOf<String>("Fai una foto", "Scegli dalla galleria", "Annulla")
    var mBtn: ImageButton? = null
    var mImg: ImageView? = null
    var globalUri: Uri? = null
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    lateinit var categoriaText: String
    lateinit var storageRef: StorageReference
    lateinit var fileName: String
    var uploadRef:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        inizializzaBottomMenu()
        cameraGalleryFunction()
        creaScriviList()

        val btn_upload: ImageButton = findViewById(R.id.buttonCaricaPost)
        val categoriaField: MaterialTextView = findViewById(R.id.categoriaText)

        btn_upload.setOnClickListener{
            categoriaText = categoriaField.text as String
            if(categoriaText != "" && globalUri != null){
                uploadImageOnFBStorage()
            }
            else
                Toast.makeText(this@UploadActivity, "Seleziona immagine e categoria del post",
                    Toast.LENGTH_SHORT).show()
        }
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
            if (optionMenu[i].equals("Fai una foto")) {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val fileProvider = FileProvider.getUriForFile(
                    this, "com.example.an0nym0us.fileprovider", photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                startActivityForResult(intent, 3)
            } else if (optionMenu[i].equals("Scegli dalla galleria")) {
                var intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val fileProvider = FileProvider.getUriForFile(
                    this, "com.example.an0nym0us.fileprovider", photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                startActivityForResult(intent, 1)
            } else if (optionMenu[i].equals("Annulla")) {
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
                globalUri = Uri.fromFile(photoFile)
                var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                mImg!!.setImageBitmap(bitmap)

            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {

            var bitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            globalUri = Uri.fromFile(photoFile)

            mImg!!.setImageBitmap(bitmap)
        }
    }

    fun creaScriviList(){

        var dialog: Dialog? = null
        var textview:TextView = findViewById(R.id.categoriaText)
        var arrayList = ArrayList<String>()

        arrayList!!.add("Meme")
        arrayList!!.add("Gaming")
        arrayList!!.add("Tech")
        arrayList!!.add("Scienza")
        arrayList!!.add("Sport")
        arrayList!!.add("Attualità")
        arrayList!!.add("Politica")
        arrayList!!.add("Cucina")
        arrayList!!.add("Cinema")
        arrayList!!.add("Hobby")
        arrayList!!.add("Personale")

        textview.setOnClickListener(View.OnClickListener {
            dialog = Dialog(this@UploadActivity)
            dialog!!.setContentView(R.layout.dialog_searchable_spinner)
            dialog!!.window!!.setLayout(650, 800)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.show()

            val editText = dialog!!.findViewById<EditText>(R.id.edit_text)
            val listView = dialog!!.findViewById<ListView>(R.id.list_view)
            val adapter =
                ArrayAdapter(this@UploadActivity, android.R.layout.simple_list_item_1, arrayList!!)

            listView.adapter = adapter
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int){

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    textview.setText(adapter.getItem(position))
                    dialog!!.dismiss()}
        })
    }

    fun uploadImageOnFBStorage(){

        val uploadUser = uId
        val uploadCategory = categoriaText
        val formatterPost = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        val now1 = Date()
        val uploadOnDB = formatterPost.format(now1)
        val uploadLike = 0
        val uploadDislike = 0


        val formatter2 = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now2 = Date()
        fileName = formatter2.format(now2)
        storageRef = Firebase.storage.reference.child("post/$uId/$fileName")

        val bitmap = (mImg?.getDrawable() as BitmapDrawable).getBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        storageRef.putBytes(image).
        addOnSuccessListener {
            Toast.makeText(this@UploadActivity, "Post caricato con successo", Toast.LENGTH_SHORT)
                .show()

            storageRef.downloadUrl.addOnSuccessListener {
                Toast.makeText(this@UploadActivity, it.toString(), Toast.LENGTH_SHORT)
                    .show()
                uploadRef=it.toString()
                var database = FirebaseDatabase
                    .getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti/$uploadUser")
                var comments = arrayListOf<Commento>()
                var arrayLikes = arrayListOf<String>()
                arrayLikes.add("ok")
                var arrayDislikes = arrayListOf<String>()
                arrayDislikes.add("ok")
                comments.add(Commento("ok","ok"))
                val post = Post2(uploadOnDB,uploadRef!!,uploadDislike,uploadCategory,uploadUser,uploadLike
                    ,comments, arrayLikes, arrayDislikes)
                database.child(uploadOnDB).setValue(post).addOnSuccessListener {
                    Toast.makeText(this@UploadActivity, "post caricato", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this@UploadActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { Toast.makeText(this@UploadActivity, it.message, Toast.LENGTH_SHORT)
                .show() }

        }
            .addOnFailureListener{
                Toast.makeText(this@UploadActivity, "Errore caricamento post", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}