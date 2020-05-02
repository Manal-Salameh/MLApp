package com.example.mlproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.util.List;

public class Label extends AppCompatActivity {

    private Button captureImageBtnl, detectLabelBtnl, uploadImageBtnl;
    private ImageView imageViewl;
    private TextView textViewl;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int gallery_req = 20;
    Bitmap imageBitmapl;
    Uri imageUril = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        captureImageBtnl = findViewById(R.id.capture_imagel);
        uploadImageBtnl = findViewById(R.id.upload_imagel);
        detectLabelBtnl = findViewById(R.id.detect_labelsl);
        imageViewl = findViewById(R.id.image_viewl);
        textViewl = findViewById(R.id.text_displayl);


        textViewl.setMovementMethod(new ScrollingMovementMethod());

        captureImageBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textViewl.setText("");
            }
        });


        uploadImageBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, gallery_req);
                textViewl.setText("");
            }
        });

        detectLabelBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    detectLabels();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
            imageBitmapl = (Bitmap) extras.get("data");
            imageViewl.setImageBitmap(imageBitmapl);

        }
        else if (requestCode == gallery_req){
            imageUril = data.getData();
            setImageUri(imageUril);
            imageViewl.setImageURI(imageUril);
            try {
                imageBitmapl = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUril);
                setBitmap(imageBitmapl);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    public void setImageUri(Uri imageUri){ this.imageUril = imageUri;}
    public void setBitmap(Bitmap bitmap) {
        this.imageBitmapl = bitmap;
    }

    protected void detectLabels() throws IOException {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmapl);
        final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();

        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(final List<FirebaseVisionImageLabel> l) {

                for (int i = 0; i < l.size(); i++) {

                    textViewl.append(l.get(i).getText() + " ");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error", e.toString());
            }
        });

    }

}
