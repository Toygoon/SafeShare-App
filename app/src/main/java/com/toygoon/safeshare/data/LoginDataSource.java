package com.toygoon.safeshare.data;

import android.util.Log;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.data.model.LoggedInUser;
import com.toygoon.safeshare.http.NetworkTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        // Login process
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", username);
        map.put("user_pw", password);

        NetworkTask task = new NetworkTask(Constants.API_LOGIN_URL, map, "POST");
        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(task);
        HashMap<String, String> result = null;

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // End login process

        if (result.getOrDefault("response_code", "-1").equals(Constants.LOGIN_SUCCEED_CODE)) {
            Log.d("LoginDataSource", "login succeed");

            return new Result.Success<>(new LoggedInUser(result.get("name"), username));
        } else if (result.getOrDefault("response_code", "-1").equals(Constants.LOGIN_PASSWORD_ERROR_CODE)) {
            Log.d("LoginDataSource", "password error");

            return new Result.Error(new IOException("password_error", new Throwable("password_error")));
        }

        Log.d("LoginDataSource", "Login failed");
        return new Result.Error(new IOException("Error logging in", new Throwable("error")));
    }

    public void logout() {
        // TODO: revoke authentication
    }
}