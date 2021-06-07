package id.ac.umn.uasif633a.artgram.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.adapters.CommentListAdapter;
import id.ac.umn.uasif633a.artgram.models.Comment;

public class CommentDetailActivity extends AppCompatActivity {
    private static final String TAG = "CommentDetailActivity";
    private ArrayList<Comment> listOfComments = new ArrayList<>();
    private FirebaseUser user;
    private FirebaseFirestore db;
    private EditText etCommentInput;
    private Button btnPostComment;
    private String postId;
    private String documentId;
    private RecyclerView commentListRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        commentListRv = findViewById(R.id.activity_comment_detail_rv_comment_list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        etCommentInput = (EditText) findViewById(R.id.activity_comment_detail_et_input_comment);
        btnPostComment = (Button) findViewById(R.id.activity_comment_detail_btn_post_comment);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        getAllComment();

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void getDocumentId() {
        Query query = db.collection("posts").whereEqualTo("postId", postId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentId = document.getId();
                        break;
                    }
                } else {
                    Log.d(TAG, "onComplete: error getting document: " + task.getException());
                }
            }
        });
    }

    private void postComment() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("posting");
        progressDialog.show();

        String commentText = etCommentInput.getText().toString();
        Comment comment = new Comment(user.getDisplayName(), commentText, postId);

        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        int oldItemListCounts = listOfComments.size();
                        if (oldItemListCounts != 0) {
                            listOfComments.add(comment);
                            commentListRv.getAdapter().notifyItemInserted(oldItemListCounts + 1);
                        }
                        etCommentInput.setText("");
                        progressDialog.dismiss();
                    }
                });
    }

    private void getAllComment() {
        Query query = db.collection("comments").whereEqualTo("postId", postId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = new Comment(
                                    document.get("user").toString(),
                                    document.get("commentText").toString()
                            );
                            listOfComments.add(comment);
                        }
                        CommentListAdapter adapter = new CommentListAdapter(listOfComments, getApplicationContext());
                        commentListRv.setAdapter(adapter);
                        commentListRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    } else {
                        Log.d(TAG, "onComplete: ga ada komen");
                    }
                }
            }
        });
    }

}