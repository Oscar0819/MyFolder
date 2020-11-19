package com.eunwoo.ta_alarm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.CircleOverlay;

import java.util.ArrayList;
import java.util.List;

public class ForegroundService extends Service {
    public static final String TAG = "ForegroundService";

    LocationManager locationManager;

    public static final String GPS_PROVIDER = "gps";
    public static final String NETWORK_PROVIDER = "network";
    public static final String PASSIVE_PROVIDER = "passive";

    MainActivity mainActivity;

    int CircleOverlaySizeC2015 = 0, CircleOverlaySizeC2016 = 0, CircleOverlaySizeC2017 = 0, CircleOverlaySizeC2018 = 0, CircleOverlaySizeC2019 = 0;
    int CircleOverlaySizeO2015 = 0, CircleOverlaySizeO2016 = 0, CircleOverlaySizeO2017 = 0, CircleOverlaySizeO2018 = 0, CircleOverlaySizeO2019 = 0;
    private List<CircleOverlay> CircleOverlaysC2015 = new ArrayList<>(), CircleOverlaysC2016 = new ArrayList<>(), CircleOverlaysC2017 = new ArrayList<>(),
            CircleOverlaysC2018 = new ArrayList<>(), CircleOverlaysC2019 = new ArrayList<>();
    private List<CircleOverlay> CircleOverlaysO2015 = new ArrayList<>(), CircleOverlaysO2016 = new ArrayList<>(), CircleOverlaysO2017 = new ArrayList<>(),
            CircleOverlaysO2018 = new ArrayList<>(), CircleOverlaysO2019 = new ArrayList<>();

    NotificationCompat.Builder builder2;
    NotificationManager manager2;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public ForegroundService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreateService");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mainActivity = new MainActivity();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        ;
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "MyApp::MyWakelockTag");

        CircleOverlayCsize();
        CircleOverlayOsize();

        CircleOverlayCfor();
        CircleOverlayOfor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "onDestroyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForegroundService();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;


        }
        
        locationManager.requestLocationUpdates(GPS_PROVIDER, 10000, 0, locationListener);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ForegroundService");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("위험지역알림");
        builder.setContentText("실행 중");

        builder2 = new NotificationCompat.Builder(this, "WAlarm");

        builder2.setSmallIcon(R.mipmap.ic_launcher);
        builder2.setContentTitle("위험지역알림");
        builder2.setContentText("조심하세요. 현재 교통사고 다발지역에 위치합니다.");
        builder2.setTicker("조심하세요. 현재 교통사고 다발지역에 위치합니다.");
        builder2.setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT >= 26){
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("ForegroundService", "정보", NotificationManager
                    .IMPORTANCE_LOW));

            manager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager2.createNotificationChannel(new NotificationChannel("WAlarm", "위험지역", NotificationManager
                    .IMPORTANCE_HIGH));
        }



        startForeground(1, builder.build());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "latitude : " + location.getLatitude() + " longitude : " + location.getLongitude());

            Log.d(TAG, String.valueOf(CircleOverlaySizeC2015));

            CircleOverlayCSearching(location);
            CircleOverlayOSearching(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void CircleOverlayCfor(){
        for (int i = 0; i<CircleOverlaySizeC2015; i++){
            CircleOverlaysC2015.add(mainActivity.circleOverlaysC2015.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeC2016; i++){
            CircleOverlaysC2016.add(mainActivity.circleOverlaysC2016.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeC2017; i++){
            CircleOverlaysC2017.add(mainActivity.circleOverlaysC2017.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeC2018; i++){
            CircleOverlaysC2018.add(mainActivity.circleOverlaysC2018.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeC2019; i++){
            CircleOverlaysC2019.add(mainActivity.circleOverlaysC2019.get(i));
        }
    }

    private void CircleOverlayOfor(){
        for (int i = 0; i<CircleOverlaySizeO2015; i++){
            CircleOverlaysO2015.add(mainActivity.circleOverlaysO2015.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeO2016; i++){
            CircleOverlaysO2016.add(mainActivity.circleOverlaysO2016.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeO2017; i++){
            CircleOverlaysO2017.add(mainActivity.circleOverlaysO2017.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeO2018; i++){
            CircleOverlaysO2018.add(mainActivity.circleOverlaysO2018.get(i));
        }
        for (int i = 0; i<CircleOverlaySizeO2019; i++){
            CircleOverlaysO2019.add(mainActivity.circleOverlaysO2019.get(i));
        }
    }

    private void CircleOverlayCsize(){
        CircleOverlaySizeC2015 = mainActivity.circleOverlaysC2015.size();
        CircleOverlaySizeC2016 = mainActivity.circleOverlaysC2016.size();
        CircleOverlaySizeC2017 = mainActivity.circleOverlaysC2017.size();
        CircleOverlaySizeC2018 = mainActivity.circleOverlaysC2018.size();
        CircleOverlaySizeC2019 = mainActivity.circleOverlaysC2019.size();
    }
    private void CircleOverlayOsize(){
        CircleOverlaySizeO2015 = mainActivity.circleOverlaysO2015.size();
        CircleOverlaySizeO2016 = mainActivity.circleOverlaysO2016.size();
        CircleOverlaySizeO2017 = mainActivity.circleOverlaysO2017.size();
        CircleOverlaySizeO2018 = mainActivity.circleOverlaysO2018.size();
        CircleOverlaySizeO2019 = mainActivity.circleOverlaysO2019.size();
    }

    private void CircleOverlayCSearching(Location location){
        for (int i = 0; i < CircleOverlaySizeC2015; i++){
            if (CircleOverlaysC2015.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeC2016; i++){
            if (CircleOverlaysC2016.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeC2017; i++){
            if (CircleOverlaysC2017.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeC2018; i++){
            if (CircleOverlaysC2018.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeC2019; i++){
            if (CircleOverlaysC2019.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
    }
    private void CircleOverlayOSearching(Location location){
        for (int i = 0; i < CircleOverlaySizeO2015; i++){
            if (CircleOverlaysO2015.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeO2016; i++){
            if (CircleOverlaysO2016.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeO2017; i++){
            if (CircleOverlaysO2017.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeO2018; i++){
            if (CircleOverlaysO2018.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
        for (int i = 0; i < CircleOverlaySizeO2019; i++){
            if (CircleOverlaysO2019.get(i).getBounds().contains(new LatLng(location.getLatitude(), location.getLongitude()))){
                wakeLock.acquire();
                wakeLock.release();
                Alarm();
            }
        }
    }
    private void Alarm(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager2.notify(2, builder2.build());
        }
    }
}
