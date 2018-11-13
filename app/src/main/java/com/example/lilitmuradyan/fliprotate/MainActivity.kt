package com.example.lilitmuradyan.fliprotate

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.provider.MediaStore
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 0
    var srcBitmap = null as Bitmap?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
        rotate_right.setOnClickListener {flip_rotate_view.rotateRight()}
        rotate_left.setOnClickListener {flip_rotate_view.rotateLeft()}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            srcBitmap = manageImageFromUri(data?.data!!)
        }
        flip_rotate_view.setBitmap(srcBitmap)
        flip_rotate_view.invalidate()
    }

    private fun manageImageFromUri(imageUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver, imageUri)

        } catch (e: Exception) {
            // Manage exception ...
        }

        if (bitmap != null) {
            // Here you can use bitmap in your application ...
        }
        return bitmap
    }

}
