package id.ac.umn.uasif633a.artgram.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import id.ac.umn.uasif633a.artgram.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String AUTH_USERNAME = "auth_username";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private String username, password, email;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedUsername = sharedPref.getString(AUTH_USERNAME, "");
        // Inisialisasi instance Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi komponen UI
        etUsername = findViewById(R.id.activity_login_et_username);
        etPassword = findViewById(R.id.activity_login_et_password);
        btnLogin = findViewById(R.id.activity_login_btn_login);

        if (!(savedUsername.isEmpty())) {
            etUsername.setText(savedUsername);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Data login tidak lengkap!", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
            }
        });
    }

    private void login(String username, String password) {
        // Mencari email berdasarkan username yang diberikan terlebih dahulu,
        // karena secara default Firebase Auth hanya mendukung login melalui email dan password.
        firestore.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            if (documentSnapshot != null) {
                                Log.w(TAG, documentSnapshot.get("email").toString());
                                email = documentSnapshot.get("email").toString();
                            }
                            // Jika email berhasil ditemukan (berarti data ada),
                            // maka kita akan melakukan proses sign in disini.
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                saveData(username);
                                                Log.d(TAG, "onComplete: signinfirebaseauthsuccess " + username);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Login gagal
                                                etPassword.setText("");
                                                Toast.makeText(LoginActivity.this, "Password yang kamu masukkan salah!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } catch (NullPointerException e) {
                            Toast.makeText(LoginActivity.this, "Akun tidak ditemukan!",
                                    Toast.LENGTH_SHORT).show();
                            etUsername.setText("");
                            etPassword.setText("");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                        etPassword.setText("");
                    }
                });
    }

    private void saveData(String username) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AUTH_USERNAME, username);
        editor.commit();
    }
}