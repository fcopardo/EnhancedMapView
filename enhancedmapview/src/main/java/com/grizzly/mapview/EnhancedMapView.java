package com.grizzly.mapview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by FcoPardo on 12/12/14.
 */
public class EnhancedMapView extends BooleanFrameLayout implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient apiClient;
    private List<LatLng> poligon;
    private List<Polyline> polylines;
    private int polygonColor;
    private int polygonLineWidth;
    private LatLng centerPoint;
    private Float zoom;

    /**
     * Listener Zone
     */
    private GoogleMap.OnCameraChangeListener cameraChangeListener;
    private OnMapLoadingListener mapLoadingListener;


    public interface OnEnhancedCameraChangeListener extends GoogleMap.OnCameraChangeListener{
        @Override
        void onCameraChange(CameraPosition cameraPosition);
    }

    public interface OnMapDrawingListener{
        void onMapDrawing(List<LatLng> poligon);
    }

    public interface OnMapLoadingListener{
        void onMapLoaded(GoogleMap map);
    }

    public EnhancedMapView(Context context) {
        super(context);
        Log.e("EnhancedMapView", "Context constructor");
        inflateComponents();
    }

    public EnhancedMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("EnhancedMapView", "AttributeSet constructor");
        inflateComponents();
    }

    public EnhancedMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.e("EnhancedMapView", "Int constructor");
        inflateComponents();
    }

    private void inflateComponents(){

        if(polygonColor == 0) polygonColor = Color.BLACK;
        if(polygonLineWidth == 0) polygonLineWidth = 4;
        if(poligon == null) poligon = new LinkedList<>();
        if(polylines == null) polylines = new LinkedList<>();
        
        mapView = new MapView(getContext());

        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        mapView.setLayoutParams(params);
        addView(mapView);

        Bundle bundle = new Bundle();
        try{
            Intent intent = ((Activity)getContext()).getIntent();
            bundle = intent.getExtras() != null ? intent.getExtras() : new Bundle();
        }catch(ClassCastException | NullPointerException e){
            if(BuildConfig.DEBUG){
                Log.e("EnhancedMapView", "Bundle is null, do not worry");
                e.printStackTrace();
            }
        }

        mapView.onCreate(bundle);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getContext().getApplicationContext());
        } catch (Exception e) {
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
        mapView.getMapAsync(this);
        Log.e("EnhancedMapView", "inflated!");
    }

    public MapView getMapView() throws NullPointerException {
        return mapView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("EnhancedMapView", "Map was initialized");
        map = googleMap;

        if(cameraChangeListener != null){
            setOnCameraChangeListener(cameraChangeListener);
        }

        if(mapLoadingListener != null){
            mapLoadingListener.onMapLoaded(map);
        }

        apiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();

    }

    /**
     * Interface to be executed after the map is ready. Put here all the map
     * @param listener the interface to be executed.
     */
    public void setOnMapLoadedListener(OnMapLoadingListener listener){
        mapLoadingListener = listener;
    }

    public void setOnEnhancedCameraChangeListener(OnEnhancedCameraChangeListener listener){
        if(map!=null){
            map.setOnCameraChangeListener(camera -> {
                if(!isContentTouched() && !isCaptureTouches()){
                    listener.onCameraChange(camera);
                }
            });
        }
    }

    public void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener listener) {
        if(map != null) {
            map.setOnCameraChangeListener(camera -> {
                if(!isContentTouched()){
                    listener.onCameraChange(camera);
                    centerPoint = map.getCameraPosition().target;
                }
            });
        }else{
            cameraChangeListener = listener;
            Log.e("EnhancedMapView", "CameraChangeListener off! there was no map!");
        }
    }

    public void setOnMapDrawingListener(final OnMapDrawingListener listener){
        setOnBlockingDragListener(new OnBlockingDragListener() {
            @Override
            public void onDrag(MotionEvent motionEvent) {
                if(isCaptureTouches()){

                    float x = motionEvent.getX();
                    float y = motionEvent.getY();

                    int xCoordinate = Integer.parseInt(String.valueOf(Math.round(x)));
                    int yCoordinate = Integer.parseInt(String.valueOf(Math.round(y)));

                    Point point = new Point(xCoordinate, yCoordinate);
                    if(map != null){
                        LatLng mapCoordinates = map.getProjection().fromScreenLocation(point);

                        if (isContentTouched()) {
                            poligon.add(mapCoordinates);

                            polylines.add(map.addPolyline(new PolylineOptions()
                                    .addAll(poligon)
                                    .width(polygonLineWidth)
                                    .color(polygonColor)));
                        } else {

                            LatLng finalCoordinate = poligon.get(0);
                            poligon.add(finalCoordinate);
                            polylines.add(map.addPolyline(new PolylineOptions()
                                    .addAll(poligon)
                                    .width(polygonLineWidth)
                                    .color(polygonColor)));
                            listener.onMapDrawing(poligon);
                            poligon.clear();
                        }
                    }
                }
            }
        });
    }

    public void onResume(){
        if(map!=null){
            mapView.onResume();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(centerPoint.latitude,
                            centerPoint.longitude));
            CameraUpdate newZoom = CameraUpdateFactory.zoomTo(zoom);
            map.moveCamera(center);
            map.animateCamera(newZoom);
            if(polylines!=null && !polylines.isEmpty()){
                for(Polyline line:polylines){

                    map.addPolyline(new PolylineOptions()
                            .addAll(line.getPoints())
                            .width(line.getWidth())
                            .color(line.getColor()));
                }
            }
        }
    }

    public void onPause(){
        if(map!=null){
            zoom = map.getCameraPosition().zoom;
            centerPoint = map.getCameraPosition().target;
        }
    }

    public void clearPolylines(){
        if(map!=null && polylines!=null && !polylines.isEmpty()){
            for(Polyline line:polylines){
                line.remove();
            }
        }
    }

    public Circle addCircle(CircleOptions circleOptions){
        if(map == null) return null;
        return map.addCircle(circleOptions);
    }

    public GroundOverlay addGroundOverlay (GroundOverlayOptions options){
        if(map == null) return null;
        return map.addGroundOverlay(options);
    }

    public Marker addMarker (MarkerOptions options){
        if(map == null) return null;
        return map.addMarker(options);
    }

    public Polygon addPolygon (PolygonOptions options){
        if(map == null) return null;
        return map.addPolygon(options);
    }

    public Polyline addPolyline (PolylineOptions options){
        if(map == null) return null;
        return map.addPolyline(options);
    }

    public TileOverlay addTileOverlay (TileOverlayOptions options){
        if(map == null) return null;
        return map.addTileOverlay(options);
    }

    public void animateCamera (CameraUpdate update, GoogleMap.CancelableCallback callback){
        if(map!=null) map.animateCamera(update, callback);
    }

    public void animateCamera (CameraUpdate update){
        map.animateCamera(update);
    }

    public void animateCamera (CameraUpdate update, int durationMs, GoogleMap.CancelableCallback callback){
        map.animateCamera(update, durationMs, callback);
    }

    public void clear(){
        map.clear();
    }

    public CameraPosition getCameraPosition(){
        if(map == null) return null;
        return map.getCameraPosition();
    }

    public IndoorBuilding getFocusedBuilding(){
        if(map == null) return null;
        return map.getFocusedBuilding();
    }

    public int getMapType(){
        if(map == null) return 0;
        return map.getMapType();
    }

    public float getMaxZoomLevel(){
        if(map == null) return 0f;
        return map.getMaxZoomLevel();
    }

    public float getMinZoomLevel(){
        if(map == null) return 0f;
        return map.getMinZoomLevel();
    }

    @Deprecated
    public Location getMyLocation(){
        if(map == null) return null;
        return map.getMyLocation();
    }

    public void setLocationSource(LocationSource source) {
        if(map != null)map.setLocationSource(source);
    }

    public UiSettings getUiSettings() {
        if(map == null) return null;
        return map.getUiSettings();
    }

    public Projection getProjection() {
        if(map == null) return null;
        return map.getProjection();
    }

    public void setOnMapClickListener(GoogleMap.OnMapClickListener listener) {
        if(map != null) {
            map.setOnMapClickListener(listener);
        }
    }

    public void setOnMapLongClickListener(GoogleMap.OnMapLongClickListener listener) {
        if(map != null) {
            map.setOnMapLongClickListener(listener);
        }
    }

    public void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener listener) {
        if(map != null) {
            map.setOnMarkerClickListener(listener);
        }
    }

    public void setOnMarkerDragListener(final GoogleMap.OnMarkerDragListener listener) {
        if(map != null) {
            map.setOnMarkerDragListener(listener);
        }
    }

    public void setOnInfoWindowClickListener(final GoogleMap.OnInfoWindowClickListener listener){
        if(map != null) {
            map.setOnInfoWindowClickListener(listener);
        }
    }

    public void setOnInfoWindowLongClickListener(final GoogleMap.OnInfoWindowLongClickListener listener){
        if(map != null){
            map.setOnInfoWindowLongClickListener(listener);
        }
    }

    public void setOnInfoWindowCloseListener(final GoogleMap.OnInfoWindowCloseListener listener){
        if(map != null){
            map.setOnInfoWindowCloseListener(listener);
        }
    }

    public void setInfoWindowAdapter(final GoogleMap.InfoWindowAdapter adapter){
        if(map != null){
            map.setInfoWindowAdapter(adapter);
        }
    }

    @Deprecated
    public void setOnMyLocationChangeListener(final GoogleMap.OnMyLocationChangeListener listener){
        if(map != null){
            map.setOnMyLocationChangeListener(listener);
        }
    }

    public final void setOnMyLocationButtonClickListener(final GoogleMap.OnMyLocationButtonClickListener listener){
        if(map != null){
            map.setOnMyLocationButtonClickListener(listener);
        }
    }

    public void setOnMapLoadedCallback(final GoogleMap.OnMapLoadedCallback listener){
        if(map != null){
            map.setOnMapLoadedCallback(listener);
        }
    }

    public final void setOnGroundOverlayClickListener(final GoogleMap.OnGroundOverlayClickListener listener){
        if(map != null){
            map.setOnGroundOverlayClickListener(listener);
        }
    }

    public final void setOnCircleClickListener(final GoogleMap.OnCircleClickListener listener){
        if(map != null){
            map.setOnCircleClickListener(listener);
        }
    }

    public final void setOnPolygonClickListener(final GoogleMap.OnPolygonClickListener listener){
        if(map != null){
            map.setOnPolygonClickListener(listener);
        }
    }

    public final void setOnPolylineClickListener(final GoogleMap.OnPolylineClickListener listener){
        if(map != null){
            map.setOnPolylineClickListener(listener);
        }
    }

    public final void snapshot(GoogleMap.SnapshotReadyCallback listener){
        if(map != null){
            map.snapshot(listener);
        }
    }

    public final void snapshot(final GoogleMap.SnapshotReadyCallback listener, Bitmap bitmap) {
        if(map != null){
            map.snapshot(listener, bitmap);
        }

    }

    public final void setPadding(int var1, int var2, int var3, int var4) {
        if(map != null){
            map.setPadding(var1, var2, var3, var4);
        }
    }

    public final void setContentDescription(String string){
        if(map != null){
            map.setContentDescription(string);
        }
    }
}
