package com.example.facedectioninml

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find button that perform the task
        val buttonCamera = findViewById<Button>(R.id.btnCamera)
        buttonCamera.setOnClickListener{

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent,123) }
            // if camera permission or any other issue are occur showing toast sms
            else{ Toast.makeText(this, "oops something went wrong ", Toast.LENGTH_SHORT).show() }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap

            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun detectFace(bitmap: Bitmap){
        // High-accuracy landmark detection and face classification
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully , our face is  successfully detect
                var resultText = " "
                var i = 1
                for (face in faces){
                    resultText = "Face Number: $i" +
                            "\n Smile:  ${face.smilingProbability?.times(100)}%" +
                            "\n Left Eye Open: ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\n Right Eye Open: ${face.rightEyeOpenProbability?.times(100)}%"
                              i++
                }

                if (faces.isEmpty()){
                    Toast.makeText(this, " Face Detection is Empty ", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                else{
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception , face detection is failed
                Toast.makeText(this, " Something Went Wrong", Toast.LENGTH_SHORT).show()

            }
    }

}