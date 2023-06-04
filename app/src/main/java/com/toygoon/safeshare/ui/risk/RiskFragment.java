package com.toygoon.safeshare.ui.risk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.FragmentRiskBinding;
import com.toygoon.safeshare.http.NetworkTask;

import net.daum.mf.map.api.MapPoint;

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
        final EditText riskTitle = binding.riskFactorTitle, riskContent = binding.riskFactorContent;
        final Button riskPhoto = binding.riskFactorPhoto, riskSubmit = binding.riskSubmit;

        final String etc = getString(R.string.etc);

        // Get pre-defined RiskFactors if available Start
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
        riskSpinner.setAdapter(adapter);
        // Get pre-defined RiskFactors if available End

        // Get current location Start
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        // Request location permission start
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        // Request location permission end

        Location userLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        MapPoint userMapPoint = MapPoint.mapPointWithGeoCoord(userLocation.getLatitude(), userLocation.getLongitude());

        // Set Sample text
        riskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (riskSpinner.getSelectedItem().equals(getString(R.string.etc))) {
                    riskTitle.setText("");
                    riskContent.setText("");
                } else {
                    String current = riskSpinner.getSelectedItem().toString();
                    riskTitle.setText(String.format(getString(R.string.risk_title_sample), current));
                    riskContent.setText(String.format(getString(R.string.risk_content_sample), current));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Report risk
        riskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirm dialog
                new AlertDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.risk_label_submit))
                        .setMessage(getString(R.string.risk_confirm))
                        .setIcon(R.drawable.ic_menu_risk)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Set sos status
                                SharedPreferences sosStatus = requireActivity().getSharedPreferences("sos", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sosStatus.edit();

                                editor.putBoolean("sos", true);
                                editor.apply();

                                // Change current fragment to main
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                navController.navigate(R.id.nav_home);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}