package com.project.instaapp

import android.R.attr
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add.*


@Suppress("DEPRECATION")
class AddActivity : AppCompatActivity() {

    lateinit var imageuri : Uri
    var myUrl = ""
    lateinit var uploadTask : UploadTask
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)


        storageReference = FirebaseStorage.getInstance().getReference("posts")

        close.setOnClickListener{
            startActivity(Intent(this@AddActivity, MainActivity::class.java))
            finish()
        }

        post.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this@AddActivity)
    }

    private fun getFileExtenstion(uri: Uri): String? {
        val contentResolver = contentResolver
        var mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                imageuri = result.uri

                imagePost_added.setImageURI(imageuri)
                Toast.makeText(this@AddActivity, "$imageuri", Toast.LENGTH_LONG).show()
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this@AddActivity, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun uploadImage() {

        val progressDialog = ProgressDialog(this@AddActivity)
        progressDialog.setMessage("Posting")
        progressDialog.show()

        if(imageuri!=null){
            val filereference =storageReference.child(
                "${System.currentTimeMillis()}.${getFileExtenstion(imageuri)}"
            )
            uploadTask = filereference.putFile(imageuri)
            uploadTask.continueWith { p0 ->
                if (!p0.isSuccessful) {
                    throw p0.exception!!
                }
                filereference.downloadUrl
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    it.result!!.addOnSuccessListener {
                        myUrl = it.toString()

                        val reference = FirebaseDatabase.getInstance().getReference("Posts")
                        val postId = reference.push().key
                        val hashMap = HashMap<String, Any>()
                        hashMap["postsid"] = postId!!
                        hashMap["postimage"] = myUrl
                        hashMap["description"] = description.text.toString()
                        hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid

                        Toast.makeText(this@AddActivity, "$myUrl", Toast.LENGTH_LONG).show()

                        reference.child(postId).setValue(hashMap)
                        progressDialog.dismiss()
                        startActivity(Intent(this@AddActivity, MainActivity::class.java))
                        finish()
                    }

                }else{
                    Toast.makeText(this@AddActivity, "Failed!", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@AddActivity, "${it.message}", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this@AddActivity, "No Image Selected!", Toast.LENGTH_LONG).show()
        }
    }
}