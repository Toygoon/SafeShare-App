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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.toygoon.safeshare.MainActivity;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private EditText password2;
    private EditText realname;
    private EditText phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        this.password2 = binding.password2;
        this.realname = binding.realname;
        this.phone = binding.phone;

        if (this.phone.getVisibility() == View.VISIBLE) {

        }

        // To save user information
        SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);
//        Log.d("LoginActivity", auto.getString("userId", "None"));

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);


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
                    editor.apply();

                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
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

    private void showRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_dialog)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        binding.password2.setVisibility(View.VISIBLE);
                        binding.realname.setVisibility(View.VISIBLE);
                        binding.phone.setVisibility(View.VISIBLE);
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