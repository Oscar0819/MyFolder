package com.eunwoo.ta_alarm;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.eunwoo.ta_alarm.Cdata.Cdata2015;
import com.eunwoo.ta_alarm.Cdata.Cdata2016;
import com.eunwoo.ta_alarm.Cdata.Cdata2017;
import com.eunwoo.ta_alarm.Cdata.Cdata2018;
import com.eunwoo.ta_alarm.Cdata.Cdata2019;
import com.eunwoo.ta_alarm.Odata.Odata2015;
import com.eunwoo.ta_alarm.Odata.Odata2016;
import com.eunwoo.ta_alarm.Odata.Odata2017;
import com.eunwoo.ta_alarm.Odata.Odata2018;
import com.eunwoo.ta_alarm.Odata.Odata2019;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;

import com.naver.maps.map.OnMapReadyCallback;

import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = "LARS";

    private final int SINGLE_PERMISSION = 1004; //권한 변수

    Cdata2015 cdata2015;
    Cdata2016 cdata2016;
    Cdata2017 cdata2017;
    Cdata2018 cdata2018;
    Cdata2019 cdata2019;

    Odata2015 odata2015;
    Odata2016 odata2016;
    Odata2017 odata2017;
    Odata2018 odata2018;
    Odata2019 odata2019;

    ParsingDataList parsingDataList = new ParsingDataList();

    BackgroundExecutor executor;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    List<Marker> markersC2015 = new ArrayList<>();
    List<Marker> markersC2016 = new ArrayList<>();
    List<Marker> markersC2017 = new ArrayList<>();
    List<Marker> markersC2018 = new ArrayList<>();
    List<Marker> markersC2019 = new ArrayList<>();

    List<Marker> markersO2015 = new ArrayList<>();
    List<Marker> markersO2016 = new ArrayList<>();
    List<Marker> markersO2017 = new ArrayList<>();
    List<Marker> markersO2018 = new ArrayList<>();
    List<Marker> markersO2019 = new ArrayList<>();

    public static List<CircleOverlay> circleOverlaysC2015 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysC2016 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysC2017 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysC2018 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysC2019 = new ArrayList<>();

    public static List<CircleOverlay> circleOverlaysO2015 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysO2016 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysO2017 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysO2018 = new ArrayList<>();
    public static List<CircleOverlay> circleOverlaysO2019 = new ArrayList<>();

    int C2015size, C2016size, C2017size, C2018size, C2019size;
    int O2015size, O2016size, O2017size, O2018size, O2019size;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView()가 호출되기 전에 setRequestedOrientation()가 호출되어야함
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//ACCESS_FINE_LOCATION 권한없음

            //권한 요청 코드
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION);
        }

        setContentView(R.layout.activity_main);

/*
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//ACCESS_FINE_LOCATION 권한없음

            //권한 요청 코드
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION);
        } else { //ACCESS_FINE_LOCATION 권한이 있을 때

            FragmentManager fm = getSupportFragmentManager();
            MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

            if (mapFragment == null) {
                mapFragment = MapFragment.newInstance();
                fm.beginTransaction().add(R.id.map, mapFragment).commit();
            }

            mapFragment.getMapAsync(this);
        }
*/

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopService(intent);
        //여러 메인 엑티비티가 쌓여있는 것을 다 종료하기 위해 사용.
        ActivityCompat.finishAffinity(MainActivity.this);
        System.exit(0);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //서비스종료
                stopService(intent);
                //여러 메인 엑티비티가 쌓여있는 것을 다 종료하기 위해 사용.
                ActivityCompat.finishAffinity(MainActivity.this);
                System.exit(0);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            } else {

            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);

        executor = new BackgroundExecutor();

        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {
            // 백그라운드 스레드

            CDatafor();

            ODatafor();

            handler.post(() -> {
                // 메인 스레드
                setCDatafor();
                setODatafor();
            });
        });


        intent = new Intent(this, ForegroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            startService(intent);
        }

    }

    public void CDatafor(){
        cdata2015 = new Cdata2015(this);
        C2015size = parsingDataList.Clo_crd2015.size();

        cdata2016 = new Cdata2016(this);
        C2016size = parsingDataList.Clo_crd2016.size();

        cdata2017 = new Cdata2017(this);
        C2017size = parsingDataList.Clo_crd2017.size();

        cdata2018 = new Cdata2018(this);
        C2018size = parsingDataList.Clo_crd2018.size();

        cdata2019 = new Cdata2019(this);
        C2019size = parsingDataList.Clo_crd2019.size();

        for (int i = 0; i < C2015size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Cla_crd2015.get(i), parsingDataList.Clo_crd2015.get(i)));
            markersC2015.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Cla_crd2015.get(i), parsingDataList.Clo_crd2015.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysC2015.add(circleOverlay);
        }
        for (int i = 0; i < C2016size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Cla_crd2016.get(i), parsingDataList.Clo_crd2016.get(i)));
            markersC2016.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Cla_crd2016.get(i), parsingDataList.Clo_crd2016.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysC2016.add(circleOverlay);
        }
        for (int i = 0; i < C2017size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Cla_crd2017.get(i), parsingDataList.Clo_crd2017.get(i)));
            markersC2017.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Cla_crd2017.get(i), parsingDataList.Clo_crd2017.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysC2017.add(circleOverlay);
        }
        for (int i = 0; i < C2018size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Cla_crd2018.get(i), parsingDataList.Clo_crd2018.get(i)));
            markersC2018.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Cla_crd2018.get(i), parsingDataList.Clo_crd2018.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysC2018.add(circleOverlay);
        }
        for (int i = 0; i < C2019size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Cla_crd2019.get(i), parsingDataList.Clo_crd2019.get(i)));
            markersC2019.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Cla_crd2019.get(i), parsingDataList.Clo_crd2019.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysC2019.add(circleOverlay);
        }
    }
    public void setCDatafor(){
        for (int i = 0; i < C2015size; i++){
            markersC2015.get(i).setMap(naverMap);
            circleOverlaysC2015.get(i).setMap(naverMap);
        }
        for (int i = 0; i < C2016size; i++){
            markersC2016.get(i).setMap(naverMap);
            circleOverlaysC2016.get(i).setMap(naverMap);
        }
        for (int i = 0; i < C2017size; i++){
            markersC2017.get(i).setMap(naverMap);
            circleOverlaysC2017.get(i).setMap(naverMap);
        }
        for (int i = 0; i < C2018size; i++){
            markersC2018.get(i).setMap(naverMap);
            circleOverlaysC2018.get(i).setMap(naverMap);
        }
        for (int i = 0; i < C2019size; i++){
            markersC2019.get(i).setMap(naverMap);
            circleOverlaysC2019.get(i).setMap(naverMap);
        }
    }

    public void ODatafor(){
        odata2015 = new Odata2015(this);
        O2015size = parsingDataList.Olo_crd2015.size();

        odata2016 = new Odata2016(this);
        O2016size = parsingDataList.Olo_crd2016.size();

        odata2017 = new Odata2017(this);
        O2017size = parsingDataList.Olo_crd2017.size();

        odata2018 = new Odata2018(this);
        O2018size = parsingDataList.Olo_crd2018.size();

        odata2019 = new Odata2019(this);
        O2019size = parsingDataList.Olo_crd2019.size();

        for (int i = 0; i < O2015size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Ola_crd2015.get(i), parsingDataList.Olo_crd2015.get(i)));
            markersO2015.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Ola_crd2015.get(i), parsingDataList.Olo_crd2015.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysO2015.add(circleOverlay);
        }
        for (int i = 0; i < O2016size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Ola_crd2016.get(i), parsingDataList.Olo_crd2016.get(i)));
            markersO2016.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Ola_crd2016.get(i), parsingDataList.Olo_crd2016.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysO2016.add(circleOverlay);
        }
        for (int i = 0; i < O2017size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Ola_crd2017.get(i), parsingDataList.Olo_crd2017.get(i)));
            markersO2017.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Ola_crd2017.get(i), parsingDataList.Olo_crd2017.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysO2017.add(circleOverlay);
        }
        for (int i = 0; i < O2018size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Ola_crd2018.get(i), parsingDataList.Olo_crd2018.get(i)));
            markersO2018.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Ola_crd2018.get(i), parsingDataList.Olo_crd2018.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysO2018.add(circleOverlay);
        }
        for (int i = 0; i < O2019size; i++) {
            Marker marker = new Marker();
            CircleOverlay circleOverlay = new CircleOverlay();

            marker.setPosition(new LatLng(parsingDataList.Ola_crd2019.get(i), parsingDataList.Olo_crd2019.get(i)));
            markersO2019.add(marker);

            circleOverlay.setCenter(new LatLng(parsingDataList.Ola_crd2019.get(i), parsingDataList.Olo_crd2019.get(i)));
            circleOverlay.setColor(Color.BLUE);
            circleOverlay.setRadius(100);
            circleOverlaysO2019.add(circleOverlay);
        }
    }
    public void setODatafor(){
        for (int i = 0; i < O2015size; i++){
            markersO2015.get(i).setMap(naverMap);
            circleOverlaysO2015.get(i).setMap(naverMap);
        }
        for (int i = 0; i < O2016size; i++){
            markersO2016.get(i).setMap(naverMap);
            circleOverlaysO2016.get(i).setMap(naverMap);
        }
        for (int i = 0; i < O2017size; i++){
            markersO2017.get(i).setMap(naverMap);
            circleOverlaysO2017.get(i).setMap(naverMap);
        }
        for (int i = 0; i < O2018size; i++){
            markersO2018.get(i).setMap(naverMap);
            circleOverlaysO2018.get(i).setMap(naverMap);
        }
        for (int i = 0; i < O2019size; i++){
            markersO2019.get(i).setMap(naverMap);
            circleOverlaysO2019.get(i).setMap(naverMap);
        }
    }
}