package com.toygoon.safeshare.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toygoon.safeshare.Constants;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.FragmentHomeBinding;
import com.toygoon.safeshare.http.NetworkTask;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HashMap<String, MapPOIItem> markers;
    private HashMap<String, MapCircle> circles;
    private MapView mapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        markers = new HashMap<>();
        circles = new HashMap<>();

        if (requireActivity().getIntent().getExtras() != null) {
            // Notification case
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = new MapView(this.getContext());
        binding.mapView.addView(mapView);

        moveToCurrentLocation();

        final int RIPPLE_COLOR = requireActivity().getColor(R.color.green_700);
        final FloatingActionButton buttonPin = binding.buttonPin, buttonSos = binding.buttonSos;
        buttonPin.setRippleColor(RIPPLE_COLOR);
        buttonSos.setRippleColor(RIPPLE_COLOR);

        buttonPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, getString(R.string.move_to_current_location), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setBackgroundTint(Color.WHITE)
                        .setTextColor(Color.BLACK)
                        .show();
                 */
                moveToCurrentLocation();
            }
        });

        buttonSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergency();
            }
        });

        return root;
    }

    private Location moveToCurrentLocation() {
        // Move to current location
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

        // Request notification permission start
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {  //권한 허용상태인지 체크
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        // Request notification permission end

        Location userLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        MapPoint userMapPoint = MapPoint.mapPointWithGeoCoord(userLocation.getLatitude(), userLocation.getLongitude());

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(getString(R.string.location));
        marker.setMapPoint(userMapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.ic_map_marker);
        marker.setCustomImageAutoscale(false);

        int circleColor = Color.argb(100, 0x0D, 0x78, 0x0D);

        SharedPreferences sosStatus = requireActivity().getSharedPreferences("sos", Activity.MODE_PRIVATE);

        if (sosStatus.getBoolean("sos", false)) {
            circleColor = Color.argb(100, 0xEC, 0x40, 0x7A);
        }

        MapCircle circle = new MapCircle(userMapPoint, 100, circleColor, circleColor);
        circle.setTag(0);

        if (markers.getOrDefault("current", null) != null) {
            mapView.removePOIItem(markers.get("current"));
            markers.remove("current");
        }

        if (circles.getOrDefault("current", null) != null) {
            mapView.removeCircle(circles.get("current"));
            circles.remove("current");
        }

        markers.put("current", marker);
        circles.put("current", circle);

        mapView.addPOIItem(marker);
        mapView.addCircle(circle);
        mapView.setMapCenterPoint(userMapPoint, true);
        mapView.setZoomLevel(1, true);

        return userLocation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.removeAllViews();
        binding = null;
    }

    private void emergency() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.emergency_request_title)
                .setMessage(R.string.emergency_request_content)
                .setIcon(R.drawable.ic_menu_risk)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder realBuilder = new AlertDialog.Builder(requireActivity());
                        realBuilder.setTitle(R.string.emergency_request_title)
                                .setMessage(R.string.emergency_request_content_again)
                                .setIcon(R.drawable.ic_menu_risk)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        emergencyRequest();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
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

    private void emergencyRequest() {
        Location userLocation = moveToCurrentLocation();

        // Sending risk report process
        SharedPreferences loggedIn = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        HashMap<String, String> map = new HashMap<>();

        map.put("user", loggedIn.getString("userId", "None"));
        map.put("title", "Emergency");
        map.put("summary", "Emergency Call");
        map.put("risk_factor", "Unknown");
        map.put("lat", String.valueOf(userLocation.getLatitude()));
        map.put("lng", String.valueOf(userLocation.getLongitude()));

        NetworkTask tokenTask = new NetworkTask(Constants.API_RISK_REPORT_URL, map, "POST");
        CompletableFuture<HashMap<String, String>> future = CompletableFuture.supplyAsync(tokenTask);
        HashMap<String, String> result = null;

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        Log.d("RiskFragment", "Risk reported");

        // End sending risk report token process
    }
}