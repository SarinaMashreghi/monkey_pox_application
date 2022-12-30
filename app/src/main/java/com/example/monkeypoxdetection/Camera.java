//package com.example.monkeypoxdetection;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import java.io.IOException;
//
//public class Camera extends AppCompatActivity {
//
//    private static final int PERMISSION_CODE = 1000;
//    ImageButton captureBtn;
//    ImageView image;
//    Uri image_uri;
//    Bitmap bitmap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
//
//        image = findViewById(R.id.imgView);
//        captureBtn = findViewById(R.id.captureBtn);
//    }
//
//    public void captureImage(View v){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(checkSelfPermission(Manifest.permission.CAMERA) ==
//                    PackageManager.PERMISSION_DENIED ||
//                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                            PackageManager.PERMISSION_DENIED){
//                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permission, PERMISSION_CODE);
//
//
//            }
//            else{
//                openCamera();
//            }
//        }else{
//            openCamera();
//        }
//
//    }
//
//    private void openCamera() {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "New Picture");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
//
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
//        someActivityResultLauncher.launch(cameraIntent);
//    }
//
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//
//                        image.setImageURI(image_uri);
//
//
//                        Bundle b = new Bundle();
//                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//
//
////                        try {
////                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
//
////                        b.putParcelable("image", bitmap);
//                        i.putExtra("image_uri", image_uri);
//                        startActivity(i);
//
//                    }
//                }
//            });
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode){
//            case PERMISSION_CODE:{
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    openCamera();
//                }
//
//                else{
//                    Toast.makeText(this, "Permission denied ...",Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//}