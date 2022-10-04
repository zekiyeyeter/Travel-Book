package com.example.travelbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.travelbook.R;

import com.example.travelbook.adapter.PostAdapter;
import com.example.travelbook.databinding.ActivityMainBinding;
import com.example.travelbook.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public  class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseAuth mauth;
    private FirebaseFirestore firebaseFirestore;
    //ArrayList<Post> postArrayList;
    PostAdapter postAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mauth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();

           ArrayList<Post> postList =  getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postList);
        binding.recyclerView.setAdapter(postAdapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

public ArrayList<Post> getData(){
    ArrayList<Post> postList = new ArrayList<>();
    // public
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!= null){
                    Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
                if(value!=null){
          for(DocumentSnapshot snapshot: value.getDocuments()){
              Map<String,Object> data =snapshot.getData();

             String comment = (String) data.get("comment");
             String country=(String) data.get("country");
             String city= (String) data.get("city");
             String downloadUrl =(String) data.get("downloadUrl");
             String placeName= (String) data.get("placeName") ;
             Double lattitudeChoose = (Double) data.get("lattitude");
             Double longitudeChoose= (Double) data.get("longitude") ;
             Log.d("comment",comment);
             Log.d("dowloadUrl",downloadUrl);
             Post post =  new Post (downloadUrl,country,city,comment,placeName,lattitudeChoose,longitudeChoose);
             postList.add(post);
          }
          postAdapter.notifyDataSetChanged();

            }}

        });
        return postList;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Menu oluşturulduğunda ne olacak
        MenuInflater menuInflater = getMenuInflater(); // menu bağlayıcı
        menuInflater.inflate(R.menu.loc_menu, menu); // g_menu' nun aktiviteye bağlanması
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // seçilen itemi kontrol eder
        if (item.getItemId() == R.id.menuAddloc) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("information","new");
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
            mauth.signOut();
            Intent intentback = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentback);
            finish();
        } else if (item.getItemId() == R.id.menuGaleri) {
            Intent intenttogaleri = new Intent(MainActivity.this, GaleriActivity.class);
            startActivity(intenttogaleri);

        }
        return super.onOptionsItemSelected(item);
    }
}