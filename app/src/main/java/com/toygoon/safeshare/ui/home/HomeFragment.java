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

import com.toygoon.safeshare.databinding.FragmentHomeBinding;
import com.toygoon.safeshare.http.NetworkTask;

import java.util.HashMap;

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

        NetworkTask task = new NetworkTask("https://safeshare.toygoon.com/api/login/", map, "POST");
        HashMap<String, String> result = task.request(200);
        Log.d("TAG", result.toString());
        for (String k : result.keySet()) {
            Log.d("HomeFragment", k + " : " + result.get(k));
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}