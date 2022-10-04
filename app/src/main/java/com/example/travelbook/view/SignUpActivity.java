package com.example.travelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.travelbook.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mfAuth;
    ActivitySignUpBinding binding;
    private static final String TAG = "FirebaseAuthActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerHandler();
        mfAuth= FirebaseAuth.getInstance();
    }

   @Override
    public void onStart(){
        super.onStart();
        FirebaseUser CurrentUser= mfAuth.getCurrentUser();
        updateUI(CurrentUser);


    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, user.getEmail() + " giriş yaptı",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Henüz giriş yapmadınız!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void registerHandler(){


        binding.registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = binding.registerEMail.getText().toString();
                String password = binding.registerPassword.getText().toString();
                Boolean emailCheck = epostaKontrol(email);
                Boolean passwordCheck = sifreKontrol(password);
                if(emailCheck && passwordCheck) {
                    mfAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user= mfAuth.getCurrentUser();
                                updateUI(user);
                            }else{
                                Log.w(TAG,"Kayit Basarisiz ", task.getException());
                                Toast.makeText(SignUpActivity.this,"Kayit Basarisiz",Toast.LENGTH_SHORT).show();
                                updateUI(null);

                            }
                        }
                    });

                }

            }
        });
    }
    private Boolean epostaKontrol (String eposta) {
        if(TextUtils.isEmpty(eposta)) {
            binding.registerEMail.setError("Lütfen email giriniz");
            return false;
        }
        if(!eposta.endsWith("@gmail.com")) {
            binding.registerEMail.setError("Lütfen gmail kullanınız");
            return false;
        }
        binding.registerEMail.setError(null);
        return true;
    }
    private Boolean sifreKontrol (String sifre) {
        if(sifre.length() < 6) {
            binding.registerPassword.setError("En az 6 karekter giriniz!");
            return false;
        }
        binding.registerPassword.setError(null);
        return true;
    }
  }
