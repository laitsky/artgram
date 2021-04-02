package id.ac.umn.uasif633a.artgram.activities.Login;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.WallFeeds.WallFeedsActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDb;
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private String username, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi instance Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();

        // Inisialisasi komponen UI
        etUsername = findViewById(R.id.activity_login_et_username);
        etPassword = findViewById(R.id.activity_login_et_password);
        btnLogin = findViewById(R.id.activity_login_btn_login);

        btnLogin.setOnClickListener(v -> {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();

            if (username.equals("") || password.equals("")) {
                Toast.makeText(LoginActivity.this, "Data login tidak lengkap!", Toast.LENGTH_SHORT).show();
            } else {
                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        // Mencari email berdasarkan username yang diberikan terlebih dahulu,
        // karena secara default Firebase Auth hanya mendukung login melalui email dan password.
        firebaseDb.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        Log.w(TAG, documentSnapshot.get("email").toString());
                        email = documentSnapshot.get("email").toString();
                        // Jika email berhasil ditemukan (berarti data ada),
                        // maka kita akan melakukan proses sign in disini.
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this, WallFeedsActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Login gagal
                                        etPassword.setText("");
                                        Toast.makeText(LoginActivity.this, "Password yang kamu masukkan salah!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } catch (NullPointerException e) {
                        Toast.makeText(LoginActivity.this, "Akun tidak ditemukan!",
                                Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                        etPassword.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    etUsername.setText("");
                    etPassword.setText("");
                });
    }
}