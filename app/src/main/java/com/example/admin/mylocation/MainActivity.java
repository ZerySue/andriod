package com.example.admin.mylocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    private TextView postionTextView;
    private LocationManager locationManager;
    private String locationProvider;
    private Button startSend, endSend;

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //发送 停止 按钮
        startSend = (Button) findViewById(R.id.start);
        endSend = (Button) findViewById(R.id.end);

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

            }

        }
        startSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetLocation();

            }
        });

        endSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public void GetLocation() {

        // 获取显示地理位置的TextView
        postionTextView = (TextView) findViewById(R.id.location);
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //获取所有可用的位置提供器
        List<String> providers = locationManager.getAllProviders();

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果时GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {

            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            // return;
        }
        //获取Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            // return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);
//判断gps是否打开
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps)
        {
            Toast.makeText(this, "请打GPS", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setMessage("请打GPS");
            dialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0,int arg1)
                {
                    //转到手机设置页面
                    Intent intent=new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    //设置完返回原页面
                    startActivityForResult(intent,0);

                }
            });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0,int arg1)
                {
                    arg0.dismiss();
                }
            });
            dialog.show();
        }
        else{

        }
        Toast.makeText(this, provider.toString(), Toast.LENGTH_SHORT).show();
        if (location != null) {
            showLocation(location);
        } else if (location == null) {
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        }

        //监视地理位置变化

        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, (LocationListener) this);
        locationManager.requestLocationUpdates(provider, 10000, 0, locationListern);
    }

    private void showLocation(Location location) {
        String locationStr = "维度：" + location.getLatitude() +
                "\n" + "经度：" + location.getLongitude();
        Toast.makeText(this, locationStr, Toast.LENGTH_SHORT).show();
        EditText locEdit = (EditText) findViewById(R.id.location);
        String locVal = locEdit.getText().toString();
        //为空时 是否发送

        // String url= ConsonData.url+;
        // Log.i("发送数据","run-----"+url);
        Toast.makeText(this, locVal, Toast.LENGTH_SHORT).show();
        new Thread(new AccessNetWork("GET", ConsonData.url, "?dingwbh=" + locVal + "&jingd=" + location.getLongitude() + "&weid=" + location.getLatitude())).start();
    }

    /*
    LocationListern 监听器
    参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
    * */
    public LocationListener locationListern = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化 重新显示
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //如果位置发生变化 重新显示

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grangtResults) {
        if (requestCode == 2) {
            if (grangtResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetLocation();
            } else {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grangtResults);
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListern);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
