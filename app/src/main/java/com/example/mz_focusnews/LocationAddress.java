package com.example.mz_focusnews;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.example.mz_focusnews.NewsCrawling.NewsSourceConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {

    private Context context;
    private FusedLocationProviderClient locationProviderClient;
    private NewsSourceConfig newsSourceConfig;

    public LocationAddress(Context context, NewsSourceConfig newsSourceConfig) {
        this.context = context;
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.newsSourceConfig = newsSourceConfig;
    }

    public interface AddressCallback {
        void onAddressRetrieved(String address);
        void onError(String errorMessage);
    }

    @SuppressLint("MissingPermission")
    public void getAddress(final AddressCallback callback) {
        locationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        convertLocationToAddress(location, callback);
                    } else {
                        callback.onError("위치 정보를 가져올 수 없습니다.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("위치 정보 조회 실패: " + e.getMessage()));
    }

    private void convertLocationToAddress(Location location, AddressCallback callback) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressDetails = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressDetails.append(address.getAddressLine(i));
                    if (i != address.getMaxAddressLineIndex()) {
                        addressDetails.append(", ");
                    }
                }
                callback.onAddressRetrieved(addressDetails.toString());
                newsSourceConfig.setLocation(addressDetails.toString()); // 위치 정보를 NewsSourceConfig로 전달

            } else {
                callback.onError("주소 정보를 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            callback.onError("주소 변환 실패: " + e.getMessage());
        }
    }
}

