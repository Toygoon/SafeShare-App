package com.toygoon.safeshare.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.toygoon.safeshare.R;
import com.toygoon.safeshare.databinding.FragmentHomeBinding;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HashMap<String, MapPOIItem> markers;
    private MapView mapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        markers = new HashMap<>();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = new MapView(this.getContext());
        binding.mapView.addView(mapView);

        moveToCurrentLocation();

        final String RIPPLE_COLOR = "#757575";
        final FloatingActionButton buttonPin = binding.buttonPin, buttonSos = binding.buttonSos;
        buttonPin.setRippleColor(Color.parseColor(RIPPLE_COLOR));
        buttonSos.setRippleColor(Color.parseColor(RIPPLE_COLOR));

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

        return root;
    }

    private void moveToCurrentLocation() {
        // Move to current location
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        // Request location permission start
        if (ContextCompat.checkSelfPermission(getContext(),
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

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(getString(R.string.location));
        marker.setMapPoint(userMapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        if (markers.getOrDefault("current", null) != null) {
            mapView.removePOIItem(markers.get("current"));
            markers.remove("current");
        }

        markers.put("current", marker);
        mapView.addPOIItem(marker);
        mapView.setMapCenterPoint(userMapPoint, true);
        mapView.setZoomLevel(1, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}