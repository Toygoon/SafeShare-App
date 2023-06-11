package com.toygoon.safeshare.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.MainActivity;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.ActivityLoginBinding;
import com.toygoon.safeshare.http.NetworkTask;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private EditText password2;
    private EditText realname;
    private EditText phone;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        passwordEditText = binding.password;
        final Button loginButton = binding.login;
//        final ProgressBar loadingProgressBar = binding.loading;
        this.password2 = binding.password2;
        this.realname = binding.realname;
        this.phone = binding.phone;

        // To save user information
        SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);
//        Log.d("LoginActivity", auto.getString("userId", "None"));

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (phone.getVisibility() == View.VISIBLE) {
                    // Password Check
                    String pw1 = String.valueOf(passwordEditText.getText()),
                            pw2 = String.valueOf(password2.getText());

                    if (!pw1.trim().equals(pw2.trim())) {
                        showPasswordCheckFailed();
                        return;
                    }

                    String username = String.valueOf(usernameEditText.getText()),
                            name = String.valueOf(realname.getText());

                    // Register process
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user_id", username);
                    map.put("user_pw", pw1);
                    map.put("name", name);
                    map.put("mobile", String.valueOf(phone.getText()));

                    NetworkTask task = new NetworkTask(Constants.API_REGISTER_URL, map, "POST");
                    CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(task);
                    HashMap<String, String> result = null;

                    try {
                        result = future.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // End Register process

                    SharedPreferences.Editor editor = auto.edit();

                    editor.putString("userId", username);
                    editor.putString("userPw", pw1);
                    editor.putString("name", name);
                    editor.apply();

                    updateUiWithUser(null);
                    return;
                }

                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
                    Log.d("Error", loginResult.getError().toString());
                    showRegister();
                } else if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);

                    // TODO: Save the logged in user data
                    SharedPreferences.Editor editor = auto.edit();

                    editor.putString("userId", loginResult.getSuccess().getUserId());
                    editor.putString("name", loginResult.getSuccess().getName());
                    editor.putString("userPw", String.valueOf(passwordEditText.getText()));
                    editor.apply();

                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getName();
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        // Logged in successfully, go to MainActivity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showPasswordCheckFailed() {
        Toast.makeText(getApplicationContext(), R.string.password_check_failed, Toast.LENGTH_SHORT).show();
    }

    private void showRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_dialog)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Make input visible
                        binding.password2.setVisibility(View.VISIBLE);
                        binding.realname.setVisibility(View.VISIBLE);
                        binding.phone.setVisibility(View.VISIBLE);

                        // Change header text
                        binding.headerText.setText(getString(R.string.action_register));
                        binding.login.setText(getString(R.string.action_register));

                        // Move some components aligned
                        final ConstraintLayout container = binding.container;
                        ConstraintSet set = new ConstraintSet();
                        set.clone(container);
                        set.setMargin(R.id.header_text, ConstraintSet.TOP, 200);
                        set.connect(R.id.username, ConstraintSet.TOP, R.id.header_text, ConstraintSet.BOTTOM, 100);
                        set.connect(R.id.login, ConstraintSet.TOP, R.id.phone, ConstraintSet.BOTTOM, 100);
                        set.applyTo(container);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        builder.create();
        builder.show();
    }
}