package id.ac.umn.uasif633a.artgram.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.UUID;

import id.ac.umn.uasif633a.artgram.R;

public class UploadActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore db;
    Uri imageUri;
    String uploadedImageUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, image_added;
    TextView post;
    EditText caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        caption = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts/" + user.getDisplayName());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(UploadActivity.this);
    }

    private void uploadImage() {
        String postId = UUID.randomUUID().toString();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(postId);

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        uploadedImageUrl = downloadUri.toString();

                        HashMap<String, Object> newPost = new HashMap<>();

                        newPost.put("owner", user.getDisplayName());
                        newPost.put("postId", postId);
                        newPost.put("url", uploadedImageUrl);
                        newPost.put("caption", caption.getText().toString());
                        newPost.put("likes", 0);

                        db.collection("posts")
                                .add(newPost)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progressDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        startActivity(new Intent(UploadActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(UploadActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();

                image_added.setImageURI(imageUri);
            } else {
                Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}