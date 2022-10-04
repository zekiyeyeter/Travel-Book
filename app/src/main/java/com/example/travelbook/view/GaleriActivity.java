package com.example.travelbook.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.travelbook.R;
import com.example.travelbook.databinding.ActivityGaleriBinding;
import com.example.travelbook.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class GaleriActivity extends AppCompatActivity {
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;


    Uri imageData;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityGaleriBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityGaleriBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);
        registerEventHandler();
        registerLauncher();

        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();



    }
    private void registerEventHandler(){
        binding.imageView.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(GaleriActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(GaleriActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view , "permission needded for galari",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // req per
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();

                }else{
                    // req per
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }

            }else{
                //galeri
                Intent galeriIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(galeriIntent);

            }
        });
        binding.savePlace.setOnClickListener(view -> {

            if(imageData!=null){
                UUID uuid = UUID.randomUUID();
                String imageName = "images/" + uuid + ".jpg";
                storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Download URL
                        StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent imap= getIntent();
                               String placeName= imap.getStringExtra("placeNAME");
                               Double lattitudeChoose= imap.getDoubleExtra("lattitude",0.0);
                               Double longitudeChoose=imap.getDoubleExtra("longitude",0.0);


                                String downloadUrl = uri.toString();
                                String comment = binding.editTextComment.getText().toString();
                                String country = binding.editTextcountry.getText().toString();
                                String city= binding.editTextcity.getText().toString();
                                FirebaseUser user =firebaseAuth.getCurrentUser();
                                String email =user.getEmail();



                                HashMap<String, Object> postData = new HashMap<>();
                                postData.put("placeName",placeName);
                                postData.put("lattitude",lattitudeChoose);
                                postData.put("longitude",longitudeChoose);
                                postData.put("email",email);
                                postData.put("downloadUrl",downloadUrl);
                                postData.put("comment",comment);
                                postData.put("date", FieldValue.serverTimestamp());
                                postData.put("city",city);
                                postData.put("country",country);



                                firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Intent i = new Intent(GaleriActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(GaleriActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GaleriActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


   /* public Bitmap makeSmallerImage(Bitmap imageView, int maximumSize){
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(imageView,width,height,true);
    }

    */


private void registerLauncher() {

    {
activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
    @Override
    public void onActivityResult(ActivityResult result) {
        if(result.getResultCode()==RESULT_OK){
            Intent intentforgaleri =result.getData();
            if(intentforgaleri != null) {
                imageData = intentforgaleri.getData();
                binding.imageView.setImageURI(imageData);

            }
        }

    }
});

    }
    permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Intent intentToGal= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGal);
            }else {
Toast.makeText(GaleriActivity.this,"Permission Needed",Toast.LENGTH_SHORT).show();
            }
        }
    });
   }



}
