package com.example.an0nym0us

import android.Manifest
import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.impostazioni_profilo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"

class ProfileActivity : AppCompatActivity() {
    var optionMenu = arrayOf<String>("Scegli dalla galleria", "Annulla")
    var globalUri: Uri? = null
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
    private lateinit var postAdapter: PostRecyclerAdapterGrid
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefInfo: DatabaseReference
    private lateinit var listFull: ArrayList<Post2>
    private lateinit var mImg: ImageView
    private lateinit var proPic: String
    lateinit var storageRef: StorageReference
    lateinit var fileName: String
    private var listNicknames = arrayListOf<String>()
    private var canEdit: String = "false"
    private var canBeFound: String = "false"
    private var trovato: Boolean = false
    private var myNickname: String? = null
    var uploadRef:String? = null
    var imageProfile: CircleImageView? = null
    lateinit var requestOptions: RequestOptions


    private val cropActivityResultContract = object :
        ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(10,10)
                .getIntent(this@ProfileActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        overridePendingTransition(0, 0)
        imageProfile = findViewById(R.id.profilePic)
        inizializzaBottomMenu()
        inizializzaImpostazioni()
        initRecyclerView()


        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                globalUri = it
                mImg.setImageURI(globalUri)
                uploadImage()
            }
        }
    }

    private fun getPostData() {
        dbRef =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti")

        dbRefInfo =
            FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("InfoUtenti")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (listFull.isEmpty()) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            for (postSnapshot in userSnapshot.children) {

                                var postApp = postSnapshot.getValue() as HashMap<*, *>
                                var user = postApp["user"].toString()
                                var category = postApp["category"].toString()
                                var date = postApp["date"].toString()
                                var image = postApp["image"].toString()
                                var likes = postApp["likes"].toString()
                                var dislikes = postApp["dislikes"].toString()
                                var comments = postApp["comments"] as ArrayList<Commento>?
                                var arrayLikes = postApp["arrayLikes"] as ArrayList<String>?
                                var arrayDislikes = postApp["arrayDislikes"] as ArrayList<String>?
                                var post = Post2(
                                    date,
                                    image,
                                    dislikes.toInt(),
                                    category,
                                    user,
                                    likes.toInt(),
                                    comments,
                                    arrayLikes,
                                    arrayDislikes
                                )

                                if (user.equals(uId)) {
                                    listFull.add(0, post)
                                }
                            }
                        }


                        postAdapter = PostRecyclerAdapterGrid(listFull)

                        val mFragmentManager = supportFragmentManager
                        val mFragmentTransaction = mFragmentManager.beginTransaction()
                        val mFragment = PostFragment()

                        postAdapter!!.onImageClick = {
                            grid_post.visibility = View.INVISIBLE
                            linear_layout_profile.visibility = View.INVISIBLE

                            val mBundle = Bundle()

                            val post = Post(
                                it.user!!,
                                it.category!!,
                                it.date!!,
                                it.image!!,
                                it.likes,
                                it.dislikes
                            )

                            mBundle.putParcelable("post", post)
                            mBundle.putString("nameActivity", "ProfileActivity")
                            mBundle.putString("userid", uId)
                            mFragment.arguments = mBundle
                            mFragmentTransaction.add(R.id.fragment_containerProfile, mFragment)
                                .commit()
                        }

                        grid_post.adapter = postAdapter
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbRefInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentUserId: String
                if (snapshot.exists()) {
                    for (user in snapshot.children) {

                        var Map = user.getValue() as HashMap<*, *>
                        var nickname = Map["nickname"].toString()

                        if (user.key == uId) {
                            currentUserId = user.key!!
                            proPic = Map["proPic"].toString()
                            var approvazioni = Map["approvazioni"].toString()
                            myNickname=nickname
                            canEdit = Map["canEdit"].toString()
                            canBeFound = Map["canBeFound"].toString()
                            var seguiti = Map["seguiti"] as ArrayList<String>
                            var user = Utente(
                                proPic,
                                nickname,
                                approvazioni.toInt(),
                                canEdit.toBoolean(),
                                canBeFound.toBoolean(),
                                seguiti
                            )

                            setImage()

                            //setto valore della progressbar
                            progress_bar.setProgress(approvazioni.toInt())
                            var maxValue = progress_bar.max
                            if (progress_bar.progress >= maxValue) {
                                dbRefInfo.child(uId).child("canEdit").setValue(true)
                            } else
                                dbRefInfo.child(uId).child("canEdit").setValue(false)

                            //setto valore del nome del profilo (anonimo/nickname)
                            userCodeProfile.text = myNickname

                            /*verifico se l'user che sta usando l'app è lo stesso della pagina
                            e in quel caso rendo non visibile il pulsante segui*/
                            if (uId.equals(currentUserId))
                                followButton.visibility = View.INVISIBLE
                        }

                        listNicknames.add(nickname)

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setImage(){
        if(!this.isFinishing && !proPic.equals("ok")){
            Glide.with(this@ProfileActivity).load(proPic).into(imageProfile!!)
        }
    }

    private fun initRecyclerView() {
        grid_post.layoutManager = GridLayoutManager(this, 3)
        listFull = arrayListOf<Post2>()
        getPostData()
    }

    fun inizializzaBottomMenu() {
        var bottomNavigationMenuView: BottomNavigationView = findViewById(R.id.bottom_home)
        bottomNavigationMenuView.setSelectedItemId(R.id.profile)

        bottomNavigationMenuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    val intent =
                        Intent(this@ProfileActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.explore -> {
                    val intent =
                        Intent(this@ProfileActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.upload -> {
                    val intent =
                        Intent(this@ProfileActivity, UploadActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.notifications -> {
                    val intent =
                        Intent(this@ProfileActivity, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            true
        }
    }

    fun inizializzaImpostazioni() {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        var dialog: Dialog?

        settings.setOnClickListener {
            dialog = Dialog(this@ProfileActivity)
            dialog!!.setContentView(R.layout.impostazioni_profilo)
            dialog!!.window!!.setLayout(width-50, LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setGravity(Gravity.CENTER)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

            if (this@ProfileActivity != null && !this@ProfileActivity.isFinishing)
                dialog!!.show()

            val buttonNickname = dialog!!.findViewById<Button>(R.id.buttonChangeNickname)
            val buttonPhoto = dialog!!.findViewById<Button>(R.id.buttonChangePhoto)

            if (canEdit.equals("false")) {
                buttonNickname.isClickable = false
                buttonPhoto.isClickable = false
            } else {
                buttonNickname.isClickable = true
                buttonPhoto.isClickable = true
            }

            buttonNickname.setOnClickListener {
                val EditTextName = dialog!!.findViewById<EditText>(R.id.newNicknameText)
                val newName: String = EditTextName.text.toString().trim { it <= ' ' }
                if (newName.equals(""))
                    Toast.makeText(this, "Devi inserire un nuovo nome", Toast.LENGTH_SHORT).show()
                else {
                    if (listNicknames.contains(newName))
                        Toast.makeText(
                            this@ProfileActivity, "Nickname già in uso",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        dbRefInfo.child("$uId").child("nickname").setValue(newName)
                        Toast.makeText(
                            this@ProfileActivity,
                            "Nickname cambiato con successo",
                            Toast.LENGTH_SHORT
                        ).show()
                        dbRefInfo.child("$uId").child("canBeFound").setValue(true)
                        listNicknames.clear()
                    }
                }
            }

            buttonPhoto.setOnClickListener{
                mImg = dialog!!.findViewById<ImageView>(R.id.newProfilePicture)

                Gallery()

            }
        }
    }

    fun Gallery(){
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        photoFile = File.createTempFile(FILE_NAME, ".jpg", storageDir)

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

    fun chooseImage(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setItems(optionMenu) { dialogInterface, i ->
            /*if (optionMenu[i].equals("Fai una foto")) {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val fileProvider = FileProvider.getUriForFile(
                    this, "com.example.an0nym0us.fileprovider", photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                //startActivityForResult(intent, 3)
                cropActivityResultLauncher.launch(null)
            } else */if (optionMenu[i].equals("Scegli dalla galleria")) {
                var intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val fileProvider = FileProvider.getUriForFile(
                    this, "com.example.an0nym0us.fileprovider", photoFile
                )

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                cropActivityResultLauncher.launch(null)


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
        /*if (requestCode == 1 && resultCode == RESULT_OK) {
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
        }*/
        if(requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE){
            var result = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                globalUri = result.uri
            }
        }
    }

    fun uploadImage(){
        val uploadUser = uId
        val formatterImg = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val uploadOnDB = formatterImg.format(now)

        fileName = uId
        storageRef = Firebase.storage.reference.child("profile_pictures/$fileName")

        val bitmap = (mImg?.getDrawable() as BitmapDrawable).getBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()

        storageRef.putBytes(image).addOnSuccessListener{
            Toast.makeText(this,"Immagine caricata", Toast.LENGTH_SHORT).show()

            storageRef.downloadUrl.addOnSuccessListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                    .show()
                uploadRef=it.toString()
                var database = FirebaseDatabase
                    .getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("InfoUtenti/$uId")
                database.child("proPic").setValue(uploadRef).addOnSuccessListener {
                    Toast.makeText(this, "Image profilo aggiornata", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                .show() }
        }.addOnFailureListener{
            Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}