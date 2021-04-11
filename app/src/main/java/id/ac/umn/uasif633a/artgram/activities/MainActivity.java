package id.ac.umn.uasif633a.artgram.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.fragments.ExploreFragment;
import id.ac.umn.uasif633a.artgram.fragments.HomeFragment;
import id.ac.umn.uasif633a.artgram.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WallFeedsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.d(TAG, "onCreate: getting user email...: " + email);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_explore:
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        default:
                            selectedFragment = new HomeFragment();
                    }
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

                    return true;
                }
            };
}