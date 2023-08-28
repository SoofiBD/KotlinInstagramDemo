package com.example.kotlininstagramdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.example.kotlininstagramdemo.databinding.ActivityFeedBinding
import com.example.kotlininstagramdemo.databinding.ActivityPostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore

    var imageData : android.net.Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()


        registerLauncher()

    }

    fun upload(view: View){

        val uuid = java.util.UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val referance = storage.reference
        val imagesReferance = referance.child("images").child(imageName)
        //val imagesReferance = referance.child("images").child(imageData!!.lastPathSegment!!)

        if(imageData != null){
            imagesReferance.putFile(imageData!!).addOnSuccessListener { taskSnapshot ->
                val uploadedPictureReferance = FirebaseStorage.getInstance().reference.child("images").child(imageData!!.lastPathSegment!!)
                uploadedPictureReferance.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email.toString())
                    postMap.put("comment",binding.textView.text.toString())
                    postMap.put("date", com.google.firebase.Timestamp.now())

                    firestore.collection("Posts").add(postMap).addOnCompleteListener { task ->
                            finish()

                    }.addOnFailureListener { exception ->
                        Snackbar.make(view,exception.localizedMessage,Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    fun selectImage(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                //requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }.show()
            }else{
                //requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intent = android.content.Intent(android.content.Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

    }

    private fun registerLauncher(){
            activityResultLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == android.app.Activity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    imageData = intentFromResult.data
                    imageData.let {
                        binding.imageView.setImageURI(it)
                    }

                }
            }
        }

        permissionLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                val intent = android.content.Intent(android.content.Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }else{
                Snackbar.make(binding.root,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }.show()
            }
        } }


}