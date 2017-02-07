package com.songmoo.anroid.myutility;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * GPS 사용 순서
 * 1. manifest에 FINE, COARSE권한 추가.
 * 2. runtime permission 소스코트에 추가
 * 3.  GPS location manager정의
 * 4. GPS가 켜저있는지 확인. 꺼져 있다면 GPS화면으로 이동
 *
 * 5. Listener 등록
 * 5. Listener 실행
 * 6. Listener 해제
  * */
public class MainActivity extends AppCompatActivity {

    //탭 및 페이저 속성 정의
    final int TAB_COUNT = 4;
    OneFragment one;
    TwoFragment two;
    ThreeFragment three;
    FourFragment four;

    //위치정보 관리자
    private LocationManager manager;
    public LocationManager getLocationManager(){
        return manager;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Debug.startMethodTracing("trace_result");

        //프래그먼트 init
        one = new OneFragment();
        two = new TwoFragment();
        three = new ThreeFragment();
        four = new FourFragment();

        //탭 layout 정의
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("계산기"));
        tabLayout.addTab(tabLayout.newTab().setText("단위변환"));
        tabLayout.addTab(tabLayout.newTab().setText("검색"));
        tabLayout.addTab(tabLayout.newTab().setText("위치"));

        //Fragment 페이저 작성.
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        //아답터 생성.
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //페이저 리스너 : 페이저가 변경되었을때 탭을 바꾸어주는 리스너.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //탭 리스너 : 탭이 변경되었을때 페이지를 바꾸어주는 리스너.
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        //버전체크해서 마시말로우 보다 낮으면 런타임권한 체크를 하지 않는다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }

        // 메써드 추적 종료
        Debug.stopMethodTracing();

    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = one; break;
                case 1:
                    fragment = two; break;
                case 2:
                    fragment = three; break;
                case 3:
                    fragment = four; break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

        private final int REQ_CODE = 100;
        //1. 권한체크
        @TargetApi(Build.VERSION_CODES.M)
        private void checkPermission(){
            // 1.1 런타임 권한체크
            if( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ){
                // 1.2 요청할 권한 목록 작성
                String permArr[] = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                // 1.3 시스템에 권한요청
                requestPermissions(permArr, REQ_CODE);
            }else{
                init();
            }
        }
        //2. 권한체크 후 콜백 < 사용자가 확인 후 시스템이 호출하는 함수

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == REQ_CODE){
                //2.1배열이 넘긴 런타임권한을 체크해서 승인이 됐으면,
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    init(); //프로그램 실행.
                }else{
                    Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    public void init(){

        //로케이션 매니저 객체 받아오기
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //GPS센서가 켜저있는지 확인.
        if(!gpsCheck()){
            //팝업창 만들기 (빌더 생성.)
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("GPS 켜키");
            alertDialog.setMessage("GPS가 꺼져있습니다. \n 설정창으로 이동하시겠습니까?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int whcih) {
                    dialogInterface.cancel();
                }
            });
            alertDialog.show();
        }
    }
    //GPS
    private boolean gpsCheck() {
        //롤리팝 이상버전에서는 LocationManager로 gps 꺼짐 여부체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //롤리팝 이하버전에서는 LOCATION_PROVIDERS_ALLOWED로 체크.
        } else {
            String gps = android.provider.Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (gps.matches(".*gps*")) {
                return true;
            } else {
                return false;
            }
        }
    }

}


