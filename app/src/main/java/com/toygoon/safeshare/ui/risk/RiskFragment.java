package com.toygoon.safeshare.ui.risk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.FragmentRiskBinding;
import com.toygoon.safeshare.http.NetworkTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class RiskFragment extends Fragment {

    private FragmentRiskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RiskViewModel riskViewModel =
                new ViewModelProvider(this).get(RiskViewModel.class);

        binding = FragmentRiskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Spinner riskSpinner = binding.riskFactorSpinner;
        final String etc = getString(R.string.etc);

        // Get pre-defined RiskFactors if available
        NetworkTask task = new NetworkTask(Constants.API_RISK_FACTOR_URL, new HashMap<>(), "GET");
        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(task);
        HashMap<String, String> result = null;
        String[] riskFactors = null;

        try {
            result = future.get();

            if (result != null && Objects.requireNonNull(result.get("response_code")).equals("200")) {
                String raw = Objects.requireNonNull(result.get("result"));
                raw += ", ";
                raw += etc;

                riskFactors = raw.replaceAll("[\\[\\]]", "").split(", ");
            }
        } catch (Exception e) {
            riskFactors = new String[]{etc};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, riskFactors);
        Log.d("GetRiskFactor", result.toString());
        riskSpinner.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}