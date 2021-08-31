package com.example.soberme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.content.ContentValues.TAG;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageRef;
    private TextView userNameDisplay, userPhone, userEmail;
    private MaterialButton signOutBtn, resetPasswordBtn, changePictureBtn;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        signOutBtn = findViewById(R.id.sign_out_btn);
        resetPasswordBtn = findViewById(R.id.reset_password);
        changePictureBtn = findViewById(R.id.change_profile);
        //textview
        userNameDisplay = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.user_phone);
        userEmail = findViewById(R.id.user_email);

        //imageview
        profileImage = findViewById(R.id.profile_img);


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        resetPasswordBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, PasswordResetActivity.class));
        });
        signOutBtn.setOnClickListener(view -> {
            logOut();
        });
        changePictureBtn.setOnClickListener(view -> {

        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            String email = currentUser.getEmail();
            String userId = currentUser.getUid();
            boolean emailVerified = currentUser.isEmailVerified();
            //get user info from firestore
            DocumentReference docRef = fStore.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            userEmail.setText(document.getString("email"));
                            userPhone.setText(document.getString("phoneNumber"));
                            userNameDisplay.setText(document.getString("userName"));

                        } else {
                            Log.d(TAG, "No such document");

                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            //check email verification
            if (emailVerified == true) {



            } else {
//                do something if email is not verified

                Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Verify your email first",
                        Snackbar.LENGTH_INDEFINITE)
                        .show();
            }


        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}