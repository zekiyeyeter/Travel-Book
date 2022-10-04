package com.example.travelbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.travelbook.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {




    public FirebaseAuth mauth;
    private static final String TAG = "FirebaseAuthActivity";
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mauth = FirebaseAuth.getInstance();
        registerHandler();


    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser CurrentUser= mauth.getCurrentUser();
        updateUI(CurrentUser);

    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, user.getEmail() + " giriş yaptı",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Henüz giriş yapmadınız!",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void registerHandler() {
        binding.SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intentSignUp);
            }
        });


        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.editTextEMail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                Boolean emailCheck = epostaKontrol(email);
                Boolean passwordCheck = sifreKontrol(password);
                if(emailCheck && passwordCheck) {
                    mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = mauth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Log.w(TAG, "Giris Basarisiz ", task.getException());
                                Toast.makeText(LoginActivity.this, "Giris Basarisiz", Toast.LENGTH_SHORT).show();
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
            binding.editTextEMail.setError("Lütfen email giriniz");
            return false;
        }
        if(!eposta.endsWith("@gmail.com")) {
            binding.editTextEMail.setError("Lütfen gmail kullanınız");
            return false;
        }
        binding.editTextEMail.setError(null);
        return true;
    }
    private Boolean sifreKontrol (String sifre) {
        if(sifre.length() < 6) {
            binding.editTextPassword.setError("En az 6 karekter giriniz!");
            return false;
        }
        binding.editTextPassword.setError(null);
        return true;
    }
}