package id.ac.umn.uasif633a.artgram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.UserProperty;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private FirebaseFirestore firebaseDb;
    private FirebaseUser user;
    private Bundle extras;
    private EditText etFullName, etUsername, etEmail, etBio;
    private Button btnSaveEdit;
    private String fullName, username, userEmail, userBio, oldUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi instance Firebase
        firebaseDb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Inisialisasi View
        etFullName = (EditText) findViewById(R.id.activity_edit_profile_et_full_name);
        etUsername = (EditText) findViewById(R.id.activity_edit_profile_et_username);
        etEmail = (EditText) findViewById(R.id.activity_edit_profile_et_email);
        etBio = (EditText) findViewById(R.id.activity_edit_profile_et_bio);
        btnSaveEdit = (Button) findViewById(R.id.activity_edit_profile_btn_save);

        // Unpacking Bundles
        extras = getIntent().getExtras();
        if (extras != null) {
            fullName = extras.getString("FULL_NAME");
            username = extras.getString("USERNAME");
            oldUsername = extras.getString("USERNAME");
            userEmail = extras.getString("USER_EMAIL");
            userBio = extras.getString("USER_BIO");
            etFullName.setText(fullName);
            etUsername.setText(username);
            etEmail.setText(userEmail);
            etBio.setText(userBio);
        }

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasUsernameChanged = false;
                fullName = etFullName.getText().toString();
                username = etUsername.getText().toString();
                userEmail = etEmail.getText().toString();
                userBio = etBio.getText().toString();
                UserProperty userProperty = new UserProperty(userEmail, username, fullName, userBio);
                if (!oldUsername.equals(username)) {
                    hasUsernameChanged = true;
                }
                updateUserProfile(userProperty, hasUsernameChanged);
            }
        });
    }

    private void updateUserProfile(UserProperty userProperty, boolean hasUsernameChanged) {
        DocumentReference userRef;
        Map<String, Object> updatedCollection = new HashMap<>();

        updatedCollection.put("email", userProperty.getEmail());
        updatedCollection.put("full_name", userProperty.getFullName());
        updatedCollection.put("username", userProperty.getUsername());
        updatedCollection.put("bio", userProperty.getUserBio());

        /*
         Jika pengguna mengganti username, maka kita harus
         mencegah melakukan referensi ke dokumen dengan nama
         username yang baru.
         */
        if (hasUsernameChanged) {
            userRef = firebaseDb.collection("users").document(oldUsername);
        } else {
            userRef = firebaseDb.collection("users").document(userProperty.getUsername());
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userProperty.getUsername())
                .build();

        user.updateEmail(userProperty.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: email and display name successfully updated");
                                                updateUserDocument(userRef, updatedCollection, hasUsernameChanged);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void updateUserDocument(DocumentReference userRef, Map<String, Object> updatedCollection, boolean hasUsernameChanged) {
        if (hasUsernameChanged) {
            firebaseDb
                    .collection("users")
                    .document(updatedCollection.get("username").toString())
                    .set(updatedCollection)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: berhasil membuat dokumen baru");
                            firebaseDb
                                    .collection("users")
                                    .document(oldUsername)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: berhasil menghapus dokumen lama");
                                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Log.d(TAG, "onComplete: gagal menghapus dokumen lama");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: kesalahan terjadi", e);
                                        }
                                    });

                        }
                    });
        } else {
            userRef
                    .update(updatedCollection)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: whole profile data successfully updated");
                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: error updating document", e);
                        }
                    });
        }
    }
}