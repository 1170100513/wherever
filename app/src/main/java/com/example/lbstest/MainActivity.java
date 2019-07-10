package com.example.lbstest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
//import com.baidu.location.BDNotifyListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.favorite.FavoriteManager;
import com.baidu.mapapi.favorite.FavoritePoiInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaiduMap.OnMapLongClickListener,
        BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    public LocationClient mLocationClient;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyLocationListener listener = new MyLocationListener();

    private Vibrator mVibrator;
    private NotifyLister mNotifyLister;
//    private double latitude,longtitude;
//    private TextView positionText;
    private boolean isFirstLocate = true;

    // 界面控件相关
    private String location;
    private String nameText;
    private View mPop;
    private View mModify;
    EditText mdifyName;
    // 保存点中的点id
    private String currentID;
    // 现实marker的图标
    BitmapDescriptor bdA;
    List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(listener);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        bdA = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLongClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setMyLocationEnabled(true);
        requestPermission();
        requestLocation();
//        mNotifyLister = new NotifyLister();
//        mLocationClient.registerNotify(mNotifyLister);
        mLocationClient.start();
        // 初始化收藏夹
        FavoriteManager.getInstance().init();
        // 初始化UI
        initUI();
    }

    public void initUI() {
        LayoutInflater mInflater = getLayoutInflater();
        mPop = (View) mInflater.inflate(R.layout.activity_favorite_infowindow, null, false);
        getAllClick();
    }

    /**
     * 添加收藏点
     *
     */
    public void saveClick() {
        if (nameText == null || nameText.equals("")) {
            Toast.makeText(this, "名称必填", Toast.LENGTH_LONG)
                    .show();
            return;
        }
//        if (locationText.getText().toString() == null || locationText.getText().toString().equals("")) {
//            Toast.makeText(this, "坐标点必填", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }

        FavoritePoiInfo info = new FavoritePoiInfo();
        info.poiName(nameText);

        LatLng pt;
        try {
            String lat = location.substring(0, location.indexOf(","));
            String lng = location.substring(location.indexOf(",") + 1);
            double latitude = Double.parseDouble(lat);
            double longtitude = Double.parseDouble(lng);
            NotifyLister mNotifyListener = new NotifyLister();
            mNotifyListener.SetNotifyLocation(latitude,longtitude,300,mLocationClient.getLocOption().getCoorType());
            mLocationClient.registerNotify(mNotifyListener);
            pt = new LatLng(latitude, longtitude);
            info.pt(pt);
            if (FavoriteManager.getInstance().add(info) == 1) {
                Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            Toast.makeText(this, "坐标解析错误", Toast.LENGTH_LONG).show();
            return;
        }

        // 在地图上更新当前最新添加的点
//        mBaiduMap.clear();
        List<FavoritePoiInfo> list = FavoriteManager.getInstance().getAllFavPois();
        if(null == list || list.size() == 0){
            return;
        }
        MarkerOptions option = new MarkerOptions().icon(bdA).position(list.get(0).getPt());
        Bundle b = new Bundle();
        b.putString("id", list.get(0).getID());
        option.extraInfo(b);
        Marker currentMarker = (Marker) mBaiduMap.addOverlay(option);
        markers.add(currentMarker);

    }

    public void saveUI(){
        LayoutInflater mInflater = getLayoutInflater();
        mModify = (LinearLayout) mInflater.inflate(R.layout.activity_favorite_alert, null);
        mdifyName = (EditText) mModify.findViewById(R.id.modifyedittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mModify);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameText = mdifyName.getText().toString();
                if (nameText != null && !nameText.equals("")) {
                    // modify
//                    FavoritePoiInfo info = FavoriteManager.getInstance().getFavPoi(currentID);
//                    info.poiName(nameText);
//                    if (FavoriteManager.getInstance().updateFavPoi(currentID, info)) {
//                        Toast.makeText(MainActivity.this , "添加成功", Toast.LENGTH_LONG).show();
//                    }
                    saveClick();
                } else {
                    Toast.makeText(MainActivity.this, "名称不能为空，操作失败", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    /**
     * 修改收藏点
     *
     * @param v
     */
    public void modifyClick(View v) {
        //        mBaiduMap.hideInfoWindow();
        // 弹框修改
        LayoutInflater mInflater = getLayoutInflater();
        mModify = (LinearLayout) mInflater.inflate(R.layout.activity_favorite_alert, null);
        mdifyName = (EditText) mModify.findViewById(R.id.modifyedittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mModify);

        /* 避免点未收藏时，点击修改框，造成空指针异常 */
        if (null == currentID) {
            Toast.makeText(this, "该点未收藏，无法修改", Toast.LENGTH_LONG).show();
            return;
        }

        if (null == FavoriteManager.getInstance().getFavPoi(currentID)) {
            Toast.makeText(this, "获取Poi失败", Toast.LENGTH_LONG).show();
            return;
        }

        String oldName = FavoriteManager.getInstance().getFavPoi(currentID).getPoiName();
        mdifyName.setText(oldName);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = mdifyName.getText().toString();
                if (newName != null && !newName.equals("")) {
                    // modify
                    FavoritePoiInfo info = FavoriteManager.getInstance().getFavPoi(currentID);
                    info.poiName(newName);
                    if (FavoriteManager.getInstance().updateFavPoi(currentID, info)) {
                        Toast.makeText(MainActivity.this , "修改成功", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "名称不能为空，操作失败", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }


        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 删除一个指定点
     *
     * @param v
     */
    public void deleteOneClick(View v) {
        /* 避免点未收藏时，点击删除选项框，造成空指针异常 */
        if (null == currentID) {
            Toast.makeText(this, "该点未收藏，无法进行删除操作", Toast.LENGTH_LONG).show();
            return;
        }

        if (FavoriteManager.getInstance().deleteFavPoi(currentID)) {
            Toast.makeText(this, "删除点成功", Toast.LENGTH_LONG).show();
            if (markers != null) {
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getExtraInfo().getString("id").equals(currentID)) {
                        markers.get(i).remove();
                        markers.remove(i);
                        mBaiduMap.hideInfoWindow();
                        break;
                    }
                }
            }
        } else {
            Toast.makeText(this, "删除点失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取全部收藏点
     */
    public void getAllClick() {
        mBaiduMap.clear();
        List<FavoritePoiInfo> list = FavoriteManager.getInstance().getAllFavPois();
        if (list == null || list.size() == 0) {
            Toast.makeText(this, "没有收藏点", Toast.LENGTH_LONG).show();
            return;
        }
        // 绘制在地图
        markers.clear();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions option = new MarkerOptions().icon(bdA).position(list.get(i).getPt());
            Bundle b = new Bundle();
            b.putString("id", list.get(i).getID());
            option.extraInfo(b);
            markers.add((Marker) mBaiduMap.addOverlay(option));
        }

    }

    /**
     * 删除全部点
     *
     * @param v
     */
    public void deleteAllClick(View v) {
        if (FavoriteManager.getInstance().clearAllFavPois()) {
            Toast.makeText(this, "全部删除成功", Toast.LENGTH_LONG).show();
            mBaiduMap.clear();
            mBaiduMap.hideInfoWindow();
        } else {
            Toast.makeText(this, "全部删除失败", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }
    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private boolean isPermissionRequested;

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            String[] permissions = {
//                    Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.WRITE_SETTINGS,
//                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mLocationClient.removeNotifyEvent(mNotifyLister);
//        mLocationClient.unRegisterLocationListener(listener);
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
        }
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 释放收藏夹功能资源
        FavoriteManager.getInstance().destroy();
        bdA.recycle();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        mBaiduMap = null;
        super.onDestroy();
    }


    @Override
    public void onMapLongClick(LatLng point) {
        location = String.valueOf(point.latitude) + "," + String.valueOf(point.longitude);
        MarkerOptions ooA = new MarkerOptions().position(point).icon(bdA);
//        mBaiduMap.clear();
        mBaiduMap.addOverlay(ooA);
        saveUI();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mBaiduMap.hideInfoWindow();
        if (marker == null) {
            return false;
        }

        InfoWindow mInfoWindow = new InfoWindow(mPop, marker.getPosition(), -47);
        mBaiduMap.showInfoWindow(mInfoWindow);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.setMapStatus(update);

        if (null == marker.getExtraInfo()) {
            return false;
        }

        currentID = marker.getExtraInfo().getString("id");
        return true;
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    public class NotifyLister extends BDNotifyListener {
        public void onNotify(BDLocation mlocation, float distance){
            mVibrator.vibrate(1000);//振动提醒已到设定位置附近
            Toast.makeText(MainActivity.this, "震动提醒", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof  NotifyLister){
                NotifyLister notifyLister = (NotifyLister)obj;
                return this.mLatitude == notifyLister.mLatitude && this.mLongitude == notifyLister.mLongitude;
            }
            return false;
        }
    }

}
