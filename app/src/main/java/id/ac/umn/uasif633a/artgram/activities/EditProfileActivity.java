package id.ac.umn.uasif633a.artgram.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.UserProperty;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private FirebaseFirestore firebaseDb;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private Bundle extras;
    private EditText etFullName, etUsername, etEmail, etBio;
    private Button btnSaveEdit;
    private String fullName, username, userEmail, userBio, oldUsername;
    private CircleImageView profileImageView;
    private LinearLayout profileChangeBtn;
    private Uri imageUri;
    private StorageReference storageProfilePicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi instance Firebase
        firebaseDb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // Inisialisasi View
        etFullName = (EditText) findViewById(R.id.activity_edit_profile_et_full_name);
        etUsername = (EditText) findViewById(R.id.activity_edit_profile_et_username);
        etEmail = (EditText) findViewById(R.id.activity_edit_profile_et_email);
        etBio = (EditText) findViewById(R.id.activity_edit_profile_et_bio);
        btnSaveEdit = (Button) findViewById(R.id.activity_edit_profile_btn_save);
        profileImageView = (CircleImageView) findViewById(R.id.activity_edit_profile_iv_display_picture);
        profileChangeBtn = (LinearLayout) findViewById(R.id.activity_edit_profile_ll_image_profile);

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImageView);
        }

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
                databaseReference.child(user.getUid()).setValue(userProperty);
                if (!oldUsername.equals(username)) {
                    hasUsernameChanged = true;
                }
                updateUserProfile(userProperty, hasUsernameChanged);
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(EditProfileActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
            uploadProfileImage(imageUri);
        } else {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage(Uri imageUri) {

        StorageReference profileRef = storageProfilePicsRef.child("users/" + user.getDisplayName());

        profileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "firebase storage uri: " + uri);
                                UserProfileChangeRequest updateDisplayPicture = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(imageUri)
                                        .build();

                                user.updateProfile(updateDisplayPicture)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: berhasil mengganti profile picture!");
                                                }
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
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