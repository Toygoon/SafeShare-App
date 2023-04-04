package com.toygoon.safeshare.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.databinding.FragmentHomeBinding;
import com.toygoon.safeshare.http.NetworkTask;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", "hi");
        map.put("user_pw", "hi");

        NetworkTask task = new NetworkTask(Constants.API_LOGIN_URL, map, "POST");
        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(task);
        HashMap<String, String> result = null;

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}