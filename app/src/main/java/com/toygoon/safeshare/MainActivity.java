package com.toygoon.safeshare;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.toygoon.safeshare.databinding.ActivityMainBinding;
import com.toygoon.safeshare.http.NetworkTask;
import com.toygoon.safeshare.ui.login.LoginActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());


        this.getHashKey();

        // Logged in check
        SharedPreferences loggedIn = getSharedPreferences("user", Activity.MODE_PRIVATE);
        if (loggedIn.getString("userId", "None").equals("None")) {
            // If not logged in, send intent to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Logged in, just open this activity normally
            setContentView(binding.getRoot());
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        // Make status bar white
//        getWindow().setStatusBarColor(Color.WHITE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        navigationView.getBackground().setAlpha(240);

        // Make user image circle
        final ImageView navPic = navigationView.getHeaderView(0).findViewById(R.id.nav_pic);
        navPic.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Change user name and phone in navigation bar
        final TextView navId = navigationView.getHeaderView(0).findViewById(R.id.nav_id);
        final TextView navName = navigationView.getHeaderView(0).findViewById(R.id.nav_name);

        navId.setText(loggedIn.getString("userId", "None"));
        navName.setText(loggedIn.getString("name", "None"));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_rescue)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, token);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                        // Refreshing token process
                        HashMap<String, String> tokenMap = new HashMap<>();

                        tokenMap.put("user_id", loggedIn.getString("userId", "None"));
                        tokenMap.put("token", token);

                        NetworkTask tokenTask = new NetworkTask(Constants.API_TOKEN_URL, tokenMap, "POST");
                        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(tokenTask);
                        HashMap<String, String> result = null;

                        try {
                            result = future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("FirebaseMessaging", "Updating token completed");
                        // End Refreshing token process
                    }
                });
    }

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));    // 해시키를 로그로 찍어서 확인
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}