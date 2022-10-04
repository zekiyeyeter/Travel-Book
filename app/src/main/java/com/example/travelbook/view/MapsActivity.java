package com.example.travelbook.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.travelbook.R;
import com.example.travelbook.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.travelbook.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private FirebaseAuth mauth;
    private FirebaseFirestore firebaseFirestore;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> activityPermissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    boolean lastLoc ;
    Double lattitudeChoose;
    Double longitudeChoose;
    Post selectedpost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mauth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerLauncher();
        registerEventHandler();
        sharedPreferences= this.getSharedPreferences("com.example.travelbook", MODE_PRIVATE);
        lastLoc=false;
        lattitudeChoose=0.0;
        longitudeChoose=0.0;
        binding.saveMapButton.setEnabled(false);

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        String information = intent.getStringExtra("information");

        if(information.equals("new")){
            binding.saveMapButton.setVisibility(View.VISIBLE);
            binding.deleteMapButton.setVisibility(View.GONE);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    //System.out.println("location: "  + location.toString() );
                    //Konum değişince zoom yapılacak.

                    lastLoc = sharedPreferences.getBoolean("lastLoc", false);
                    if (!lastLoc) {

                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        sharedPreferences.edit().putBoolean("lastLoc",true).apply();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LocationListener.super.onStatusChanged(provider, status, extras);
                }
            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.getRoot(),"permission needed for map", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //permission
                                    activityPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                                }
                            }
                    ).show();
                }else{
                    //permission
                    activityPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                {

                }
            } else{
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener); // güncel konum isteniyor.
                // son bilinen konum
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation != null) {
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
                mMap.setMyLocationEnabled(true);
            }
        }else{
            mMap.clear();
            selectedpost = (Post)intent.getSerializableExtra("placetogo");
            binding.placeNameTextMap.setText(selectedpost.placeName);
            LatLng latLng= new LatLng(selectedpost.lattitudeChoose,selectedpost.longitudeChoose);
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedpost.placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            binding.saveMapButton.setVisibility(View.GONE);
            binding.deleteMapButton.setVisibility(View.VISIBLE);
        }


    }
    private  void registerLauncher() {

        activityPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastKnownLocation != null) {
                            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        }

                        else {
                            Toast.makeText(MapsActivity.this, "PERMISSION NEEDED", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) { // haritada bir yere uzun tıklanınca ne olacak
        mMap.clear(); // önceden işaretlenmiş yerler vasrsa silinecek
        mMap.addMarker(new MarkerOptions().position(latLng)); // haritadan yeri işaretledi
        lattitudeChoose= latLng.latitude; // yerin enlemi
        longitudeChoose= latLng.longitude; // yerin boylamı
        binding.saveMapButton.setEnabled(true); // buton görünür oldu

    }
    private void registerEventHandler() {
        binding.saveMapButton.setOnClickListener(new View.OnClickListener() { // save butona tıklandığında
            @Override
            public void onClick(View view) {
              String placeName=  binding.placeNameTextMap.getText().toString();
                Intent imap = new Intent(MapsActivity.this,GaleriActivity.class);
                imap.putExtra("placeNAME",placeName);
                imap.putExtra("lattitude",lattitudeChoose);
                imap.putExtra("longitude",longitudeChoose);
                startActivity(imap);
            }
        } );
    }
}


               /* HashMap<String, Object> postData = new HashMap<>();
                postData.put("placeName", placeName);
                postData.put("lattitude",lattitudeChoose);
                postData.put("longitude",longitudeChoose);

                firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent imap = new Intent(MapsActivity.this,GaleriActivity.class);

                        startActivity(imap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        */

