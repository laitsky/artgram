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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import id.ac.umn.uasif633a.artgram.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDb;
    private EditText etEmail, etPassword, etUsername, etFullName;
    private Button btnRegister;
    private String email, password, username, fullName;

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

                if (email.equals("") || password.equals("")
                        || username.equals("") || fullName.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Lengkapi data kamu!", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterActivity.this.registerAccount(email, password, username, fullName);
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

    private void registerAccount(String email, String password, String username, String fullName) {
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", username);
        newUser.put("full_name", fullName);
        newUser.put("email", email);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
}