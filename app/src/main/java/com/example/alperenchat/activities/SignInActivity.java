package com.example.alperenchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alperenchat.databinding.ActivitySignInBinding;
import com.example.alperenchat.utilities.Constans;
import com.example.alperenchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

    }
    //Signupactivity'i çalıştırıyorum
    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v->{
            if (isValidSignInDetails()){
                signIn();
            }
        });
    }

    private void signIn(){
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constans.Key_CollecTion_Users)
                .whereEqualTo(Constans.KEY_EMAİL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constans.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task->{
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() >0
                    ){
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constans.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constans.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constans.KEY_NAME,documentSnapshot.getString(Constans.KEY_NAME));
                        preferenceManager.putString(Constans.KEY_IMAGE,documentSnapshot.getString(Constans.KEY_IMAGE));
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        loading(false);
                        showToast("Unable to sign in");

                    }
                });


    }
    //sign in tuşuna tıklıyınca processbar çalıştırdım
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);

        }
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    //giriş değerleri kontrol ediliyor
    private Boolean isValidSignInDetails(){
        if (binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        }
        else{
            return true;
        }

    }

}