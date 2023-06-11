package com.toygoon.safeshare.ui.rescue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.data.RiskReportDTO;
import com.toygoon.safeshare.databinding.FragmentRescueBinding;
import com.toygoon.safeshare.http.NetworkTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RescueFragment extends Fragment {

    private FragmentRescueBinding binding;
    private ListView rescueList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RescueViewModel rescueViewModel =
                new ViewModelProvider(this).get(RescueViewModel.class);

        binding = FragmentRescueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Fetching process
        HashMap<String, String> map = new HashMap<>();
        SharedPreferences pref = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        map.put("user_id", pref.getString("userId", ""));
        map.put("user_pw", pref.getString("userPw", ""));

        NetworkTask task = new NetworkTask(Constants.API_RISK_GET_RISK_REPORT_URL, map, "POST");
        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(task);
        HashMap<String, String> result = null;

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // End Fetching process

        ArrayList<RiskReportDTO> riskReport = new ArrayList<>();

        try {
            JSONArray logArray = new JSONArray(result.get("result"));

            for(int i=0; i<logArray.length(); i++) {
                JSONObject json = logArray.getJSONObject(i);
                riskReport.add(new RiskReportDTO(json));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

//        rescueList = binding.rescueList;
//        ArrayAdapter<RiskReportDTO> adapter = new ArrayAdapter<>(requireActivity(), R.layout.rescue_adapter, riskReport);
//        rescueList.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}