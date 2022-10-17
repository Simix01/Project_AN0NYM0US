package com.example.an0nym0us

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File


//import kotlinx.android.synthetic.main.fragment_your_fragment_name.view.*

private lateinit var photoFile: File
private const val FILE_NAME="photo.jpg"

class UploadFragment : Fragment() {

    var optionMenu= arrayOf<String>("Take Photo","Choose from gallery","Exit")
    var mBtn:Button?=null
    var mImg:ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_upload, container, false)




        mBtn= view?.findViewById(R.id.buttonUpload)
        mImg= view?.findViewById(R.id.imageUpload)

        mBtn!!.setOnClickListener {
            if (checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else if (checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),2
                )
            } else if (checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != (PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    3
                )
            } else {
                val builder=AlertDialog.Builder(context)
                builder.setItems(optionMenu) { dialogInterface, i ->
                    if (optionMenu[i].equals("Take Photo")) {
                        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                       /* val storageDir= getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        photoFile=File.createTempFile(FILE_NAME,".jpg",storageDir)
                        val fileProvider= FileProvider.getUriForFile(requireContext(),"com.example.an0nym0us.fileprovider",photoFile)

                        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
*/
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
        }
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            requestCode->{
                val builder=AlertDialog.Builder(context)

                builder.setItems(optionMenu) { dialogInterface, i ->
                    if (optionMenu[i].equals("Take Photo")) {
                        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&resultCode==RESULT_OK){
            if(data!=null){
                var uri:Uri=data.getData()!!
                var inputStream = getActivity()?.getContentResolver()?.openInputStream(uri)
                var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                mImg!!.setImageBitmap(bitmap)
            }
        }
        else if(requestCode==3&&resultCode== RESULT_OK){
            var bitmap:Bitmap=data?.getExtras()?.get("data") as Bitmap






            // var bitmap:Bitmap=BitmapFactory.decodeFile(photoFile.absolutePath)

            mImg!!.setImageBitmap(bitmap)
        }
    }


}