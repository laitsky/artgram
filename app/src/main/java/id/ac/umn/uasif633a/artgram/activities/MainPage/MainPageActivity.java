package id.ac.umn.uasif633a.artgram.activities.MainPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.Login.LoginActivity;
import id.ac.umn.uasif633a.artgram.activities.Register.RegisterActivity;

public class MainPageActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Inisialisasi Button pada UI
        btnLogin = findViewById(R.id.activity_main_page_btn_login);
        btnRegister = findViewById(R.id.activity_main_page_btn_register);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainPageActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainPageActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}