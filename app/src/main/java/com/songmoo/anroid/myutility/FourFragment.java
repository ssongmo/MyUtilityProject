package com.songmoo.anroid.myutility;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    SupportMapFragment mapFragment;
    MainActivity activity;
    LocationManager manager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (MainActivity) context;
        manager = activity.getLocationManager();
    }

    public FourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // 리스너 등록

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                3000, // 통지사이의 최소 시간간격 (miliSecond) // 업데이트 간격
                10, // 통지사이의 최소 변경거리 (m)
                locationListener);
         //정보제공자로 네트워크프로바이더 등록
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                3000, // 통지사이의 최소 시간간격 (miliSecond)
                10, // 통지사이의 최소 변경거리 (m)
                locationListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        //리스너 해제
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        manager.removeUpdates(locationListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_four, container, false);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng seoul = new LatLng(37.516066, 127.019361);
        map.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 17));
    }

    //위치 이동할 시 실시간 연동하게할 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();//경도
            double latitude = location.getLatitude(); //위도
            double altitude = location.getAltitude();  //고도
            double accuracy = location.getAccuracy();  //정확도
            String provider = location.getProvider();  //위치제공자

            //내위치
            LatLng myPosition = new LatLng(latitude, longitude);
                                             //위도  //경도
            map.addMarker(new MarkerOptions().position(myPosition).title("i am here!"));
                                                       //내위치         //마커타이틀
            //화면을 내위치로 이동시키는 함수
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,18));
                                                             //내위치, 줌레벨
        }

        @Override
        //상태변경시 호출
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        //사용할 수 없었다가 사용가능해지면 호출
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };
}
