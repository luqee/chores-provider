package com.luqi.choresprovider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by luqi on 8/31/17.
 */

public class FragmentDirections extends Fragment implements OnMapReadyCallback {

    public static String TAG = "FragmentDirections";

    private FragmentDirectionsListener fragmentDirectionsListener;
    private GoogleMap mGoogleMap;
    Button btnShowCode;
    Context mContext;
    Utils mUtils;

    public interface FragmentDirectionsListener{
        void onButtonShowCode();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentDirectionsListener = (FragmentDirectionsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentDirectionsListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mUtils = new Utils(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directions, container, false);
        btnShowCode = (Button) root.findViewById(R.id.buttonShowCode);
        btnShowCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentDirectionsListener.onButtonShowCode();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_directions);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(mUtils.getFromPreferences(Utils.USER_LATITUDE)) ,Double.parseDouble(mUtils.getFromPreferences(Utils.USER_LONGITUDE)) );
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mGoogleMap.moveCamera(update);
        mGoogleMap.addMarker(new MarkerOptions().title("me")
                .position(latLng));
        LatLng latLngClient = new LatLng(Double.parseDouble(mUtils.getFromPreferences(Utils.CLIENT_LATITUDE)), Double.parseDouble(mUtils.getFromPreferences(Utils.CLIENT_LONGITUDE)));
        mGoogleMap.addMarker(new MarkerOptions().title("client")
                .position(latLngClient));

    }
}
