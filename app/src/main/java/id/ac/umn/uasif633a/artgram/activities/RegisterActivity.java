package id.ac.umn.uasif633a.artgram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDb;
    private EditText etEmail, etPassword, etUsername, etFullName;
    private Button btnRegister;
    private String email, password, username, fullName;
    private String userBio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi instance Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();

        // Inisialisasi komponen UI
        etEmail = findViewById(R.id.activity_register_et_email);
        etPassword = findViewById(R.id.activity_register_et_password);
        etUsername = findViewById(R.id.activity_register_et_username);
        etFullName = findViewById(R.id.activity_register_et_full_name);
        btnRegister = findViewById(R.id.activity_register_btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                username = etUsername.getText().toString();
                fullName = etFullName.getText().toString();
                UserProperty userProperty = new UserProperty(email, password, username, fullName, userBio);
                if (email.equals("")
                        || password.equals("")
                        || username.equals("")
                        || fullName.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Lengkapi data kamu!", Toast.LENGTH_SHORT).show();
                } else {
                    isUsernameExist(userProperty);
                    //RegisterActivity.this.registerAccount(email, password, username, fullName);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Melakukan pengecekan apabila user sudah melakukan signin
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // TODO: Implementasikan peralihan activity jika user sudah masuk
        }
    }

    private void isUsernameExist(UserProperty userProperty) {
        DocumentReference userRef = firebaseDb.collection("users").document(userProperty.getUsername());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(RegisterActivity.this, "Username telah dipakai", Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                    } else {
                        registerAccount(userProperty);
                    }
                } else {
                    Log.d(TAG, "isUsernameExist: get failed with" + task.getException());
                }
            }
        });

    }

    private void registerAccount(UserProperty userProperty) {
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", userProperty.getUsername());
        newUser.put("full_name", userProperty.getFullName());
        newUser.put("email", userProperty.getEmail());
        newUser.put("bio", userProperty.getUserBio());

        firebaseAuth.createUserWithEmailAndPassword(userProperty.getEmail(), userProperty.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest addDisplayName = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userProperty.getUsername())
                                    .build();

                            updateUsernameAndCompleteRegister(user, addDisplayName, userProperty.getUsername(), newUser);
                        } else {
                            // Daftar akun gagal
                            if (task.getException() != null) {
                                Log.w(TAG, "gagal mendaftarkan akun", task.getException());
                                Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            // Membersihkan input
                            etEmail.setText("");
                            etPassword.setText("");
                            etUsername.setText("");
                            etFullName.setText("");
                        }
                    }
                });
    }

    private void updateUsernameAndCompleteRegister(FirebaseUser user, UserProfileChangeRequest addDisplayName, String username, Map<String, String> newUser) {
        user.updateProfile(addDisplayName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Berhasil memperbarui nama di auth firebase");
                            firebaseDb.collection("users")
                                    .document(username)
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Daftar akun berhasil
                                            Log.d(TAG, "berhasil mendaftarkan akun");
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            RegisterActivity.this.startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "error menambahkan data ke koleksi", e);
                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}