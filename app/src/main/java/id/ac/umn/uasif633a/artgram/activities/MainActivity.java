package id.ac.umn.uasif633a.artgram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.fragments.ExploreFragment;
import id.ac.umn.uasif633a.artgram.fragments.HomeFragment;
import id.ac.umn.uasif633a.artgram.fragments.PeopleListFragment;
import id.ac.umn.uasif633a.artgram.fragments.ProfileFragment;
import id.ac.umn.uasif633a.artgram.interfaces.ProfileDataReceiver;

public class MainActivity extends AppCompatActivity implements ProfileDataReceiver {
    private static final String TAG = "MainActivity";
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private String fullName;
    private String username;
    private String userEmail;
    private String userBio;
    private String userDpUrl;
    private boolean doubleBackToExisPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi instance Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        if (user != null) {
            username = user.getDisplayName();
            userEmail = user.getEmail();
        }

        firestore.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            fullName = documentSnapshot.get("full_name").toString();
                            userBio = documentSnapshot.get("bio").toString();
                            userDpUrl = documentSnapshot.getString("display_picture");
                        }
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_explore:
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.nav_upload:
                            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.nav_people:
                            selectedFragment = new PeopleListFragment();
                            break;
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in,
                                        R.anim.fade_out,
                                        R.anim.fade_in,
                                        R.anim.slide_out
                                )
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        if (doubleBackToExisPressedOnce || getSupportFragmentManager().getBackStackEntryCount() != 0) {
            finishAndRemoveTask();
            return;
        }

        this.doubleBackToExisPressedOnce = true;
        Toast.makeText(getApplicationContext(), "Please click BACK once again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExisPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public String getUserBio() {
        return userBio;
    }

    @Override
    public String getUserDpUrl() {
        return userDpUrl;
    }
}