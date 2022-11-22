package com.example.monkeypoxdetection;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.monkeypoxdetection.ml.Efficientnnetb7ModelOptimized;
import com.example.monkeypoxdetection.ml.MonkeypoxModelNet3;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button select;
    Bitmap bitmap;
    ImageView img;
//    TextView txt;
    ArrayList<String> labels;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        select = findViewById(R.id.selectBtn);
        img = findViewById(R.id.image);
//        txt = findViewById(R.id.textView);
        labels = new ArrayList<>();

        labels.add("Monkeypox");
        labels.add("Other");  // not sure about the order


        selectedImage = getIntent().getParcelableExtra("image_uri");



        if (selectedImage != null) {

            Intent cropper = new Intent(getApplicationContext(), cropperActivity.class);
            cropper.putExtra("data", selectedImage.toString());
            startActivityForResult(cropper, 101);
            setImage(selectedImage);
        }


    }

    public void select(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        String[] mimeTypes = {"image/jpg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        getContent.launch(intent);

    }

    ActivityResultLauncher<Intent> getContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        selectedImage = data.getData();


                        Intent cropper = new Intent(getApplicationContext(), cropperActivity.class);
                        cropper.putExtra("data", selectedImage.toString());
                        startActivityForResult(cropper, 101);

//                        setImage(selectedImage);
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==101){
            String result = data.getStringExtra("result");
            Uri resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);
            }

            setImage(resultUri);
        }

    }

    public void predict(View v){
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        TensorImage tbuffer = TensorImage.createFrom(TensorImage.fromBitmap(resized), DataType.FLOAT32);
//        tbuffer = TensorImage.fromBitmap(resized);

        ByteBuffer byteBuffer = tbuffer.getBuffer();

        try {
//            MonkeypoxModelNet3 model = MonkeypoxModelNet3.newInstance(this);
//
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//            Log.d("shape", byteBuffer.toString());
//            Log.d("shape", inputFeature0.toString());
//            inputFeature0.loadBuffer(byteBuffer);
//
//            MonkeypoxModelNet3.Outputs outputs = model.process(inputFeature0);
//            float[] outputFeature0 = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();

            Efficientnnetb7ModelOptimized model = Efficientnnetb7ModelOptimized.newInstance(this);

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            Efficientnnetb7ModelOptimized.Outputs outputs = model.process(inputFeature0);
            float[] outputFeature0 = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            int maxInd = getMaxInd(outputFeature0);
//            txt.setText(labels.get(maxInd)+ outputFeature0[maxInd]);     old version

            Intent i = new Intent(this, prediction.class);

            Bundle b = new Bundle();

            b.putString("prediction", labels.get(maxInd));
            b.putFloat("accuracy", outputFeature0[maxInd]);
            i.putExtras(b);
            startActivity(i);

            System.out.println(labels.get(maxInd));

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    public int getMaxInd(float[] arr){
        float max = arr[0];
        int ind = 0;

        for (int i=1; i<arr.length; i++){
            if(arr[i]>max){
                max = arr[i];
                ind = i;
            }
        }

        return ind;
    }

    public void openCamera(View v){
        Intent i = new Intent(this, Camera.class);
        startActivity(i);
    }
    

    public void setImage(Uri image_uri){
        img.setImageURI(image_uri);
    }
}