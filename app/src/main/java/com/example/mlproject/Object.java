package com.example.mlproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.io.IOException;
import java.util.List;

public class Object extends AppCompatActivity {

    private Button captureImageBtno, detectObjectsBtno, uploadImageBtno;
    private ImageView imageViewo;
    private TextView textViewo;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int gallery_req = 20;
    Bitmap imageBitmapo;
    Uri imageUrio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        captureImageBtno = findViewById(R.id.capture_imageo);
        uploadImageBtno = findViewById(R.id.upload_imageo);
        detectObjectsBtno = findViewById(R.id.detect_objectso);
        imageViewo = findViewById(R.id.image_viewo);
        textViewo = findViewById(R.id.text_displayo);


        textViewo.setMovementMethod(new ScrollingMovementMethod());

        captureImageBtno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textViewo.setText("");
            }
        });


        uploadImageBtno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, gallery_req);
                textViewo.setText("");
            }
        });

        detectObjectsBtno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectObjects();

            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmapo = (Bitmap) extras.get("data");
            imageViewo.setImageBitmap(imageBitmapo);

        }
        else if (requestCode == gallery_req){
            imageUrio = data.getData();
            setImageUri(imageUrio);
            imageViewo.setImageURI(imageUrio);
            try {
                imageBitmapo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUrio);
                setBitmap(imageBitmapo);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    public void setImageUri(Uri imageUri){ this.imageUrio = imageUri;}
    public void setBitmap(Bitmap bitmap) {
        this.imageBitmapo = bitmap;
    }

    protected void detectObjects() {

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmapo);
        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .build();
        FirebaseVisionObjectDetector detector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options);
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {

            @Override
            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {
                Paint textPaint = new Paint();

                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.RED);
                p.setStrokeWidth(2);


                Bitmap bmp_Copy = imageBitmapo.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(bmp_Copy);

                for (int i = 0; i <detectedObjects.size();i++) {
                    Integer id = detectedObjects.get(i).getTrackingId();
                    Rect bounds = detectedObjects.get(i).getBoundingBox();
                    int category = detectedObjects.get(i).getClassificationCategory();
                    canvas.drawRect(bounds,p);


                    switch(detectedObjects.get(i).getClassificationCategory()){
                        case 0: canvas.drawText("UNKNOWN", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("UNKNOWN" + "\n");
                        break;
                        case 1: canvas.drawText("HOME_GOOD", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("HOME_GOOD" + "\n");
                        break;
                        case 2: canvas.drawText("FASHION_GOOD", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("FASHION_GOOD" + "\n");
                        break;
                        case 3: canvas.drawText("FOOD", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("FOOD" + "\n");
                        break;
                        case 4: canvas.drawText("PLACE", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("PLACE" + "\n");
                        break;
                        case 5: canvas.drawText("PLANT", bounds.right,bounds.bottom,textPaint);
                            textViewo.append("PLANT" + "\n");
                        break;
                    }
                }
                imageViewo.setImageBitmap(bmp_Copy);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}
