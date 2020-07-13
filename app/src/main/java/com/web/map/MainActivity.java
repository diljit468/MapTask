package com.web.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        MapboxMap.OnMapClickListener,MapboxMap.OnMapLongClickListener{

    private MapView mapView;
    private MapboxMap mapboxMapMain;
    private LatLng currentPosition = new LatLng(64.900932, -18.167040);
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;

    private static final String DIRECTIONS_LAYER_ID = "DIRECTIONS_LAYER_ID";
    private static final String LAYER_BELOW_ID = "road-label-small";

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private FeatureCollection dashedLineDirectionsFeatureCollection;


    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destOrigi1;
    private Point dest1Origi2;
    private Point dest3Origi4;
    MarkerViewManager markerViewManager;
    List<Model> models = new ArrayList<>();
    int pos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

      /*  mapboxMap.addOnMapClickListener(point -> {

            Toast.makeText(MainActivity.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();

            return true;
        });*/


    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if(mapboxMapMain.getMarkers().size() < 4){
        Toast.makeText(MainActivity.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
        MarkerOptions options=new MarkerOptions();
            try {
                options.setTitle(getCityName(point.getLatitude(),point.getLongitude()));
                options.setSnippet(getCitySnippet(point.getLatitude(),point.getLongitude()));
            } catch (IOException e) {
                e.printStackTrace();
            }
          
        options.position(new LatLng(point.getLatitude(),point.getLongitude()));
        mapboxMapMain.addMarker(options);

        if(mapboxMapMain.getMarkers().size()==1){
            origin=Point.fromLngLat(point.getLatitude(),point.getLongitude());
        }
        else if(mapboxMapMain.getMarkers().size()==2){
            destOrigi1=Point.fromLngLat(point.getLatitude(),point.getLongitude());
            getRoute(mapboxMapMain,origin , destOrigi1);


        }
       else if(mapboxMapMain.getMarkers().size()==3){
            dest1Origi2=Point.fromLngLat(point.getLatitude(),point.getLongitude());
            getRoute(mapboxMapMain,origin , destOrigi1);

        }

        else{
            dest3Origi4=Point.fromLngLat(point.getLatitude(),point.getLongitude());
            getRoute(mapboxMapMain,origin , destOrigi1);

        }


        //origin = Point.fromLngLat(-3.588098, 37.176164);

// Set the destination location to the Plaza del Triunfo in Granada, Spain.
        // destination = Point.fromLngLat(-3.601845, 37.184080);
        return true;
        }else{
            Toast.makeText(this, "Only Four location user selected", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String getCitySnippet(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        return address+"\n"+state+"\n"+country+"\n"+postalCode;
    }

    private String getCityName(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String city = addresses.get(0).getLocality();
        return city;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animator != null) {
            animator.cancel();
        }
        if (mapboxMapMain != null) {
            mapboxMapMain.removeOnMapClickListener(this);
        }
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMapMain = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

// Set the origin location to the Alhambra landmark in Granada, Spain.
            //origin = Point.fromLngLat(-3.588098, 37.176164);

// Set the destination location to the Plaza del Triunfo in Granada, Spain.
           // destination = Point.fromLngLat(-3.601845, 37.184080);


// Get the directions route from the Mapbox Directions API
          //  getRoute(mapboxMap, origin, destination);


            mapboxMap.addOnMapClickListener(MainActivity.this);
            mapboxMap.addOnMapLongClickListener(MainActivity.this);
      /*      ImageView hoveringMarker = new ImageView(MainActivity.this);
            hoveringMarker.setImageResource(R.drawable.marker);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            hoveringMarker.setLayoutParams(params);
            mapView.addView(hoveringMarker);*/

// Add the layer for the dashed directions route line
            initDottedLineSourceAndLayer(style);



        });
        markerViewManager = new MarkerViewManager(mapView, mapboxMap);

        /*
        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-57.225365, -33.213144)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-54.14164, -33.981818)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-56.990533, -30.583266)));

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

// Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                                MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))

// Adding a GeoJson source for the SymbolLayer icons.
                        .withSource(new GeoJsonSource(SOURCE_ID,
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))*/

// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
// the coordinate point. This is offset is not always needed and is dependent on the image
// that you use for the SymbolLayer icon.
                     /*   .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                        iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                )
                        )
                , new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


                    }
                });*/
        /*     this.mapboxMap = mapboxMap;

        geoJsonSource = new GeoJsonSource("source-id",
                Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                        currentPosition.getLatitude())));


        mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                        getResources(), R.drawable.red_marker));

                style.addSource(geoJsonSource);

                style.addLayer(new SymbolLayer("layer-id", "source-id")
                        .withProperties(
                                PropertyFactory.iconImage("marker_icon"),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true)
                        ));


                mapboxMap.addOnMapClickListener(MainActivity.this);

            }
        });*/
    }

    private void initDottedLineSourceAndLayer(Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(SOURCE_ID));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        DIRECTIONS_LAYER_ID, SOURCE_ID).withProperties(
                        lineWidth(4.5f),
                        lineColor(Color.BLACK),
                        lineTranslate(new Float[]{0f, 4f}),
                        lineDasharray(new Float[]{1.2f, 1.2f})
                ), LAYER_BELOW_ID);
    }

    /**
     * Update the GeoJson data that's part of the LineLayer.
     *
     * @param route The route to be drawn in the map's LineLayer that was set up above.
     */
    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMapMain != null) {
            mapboxMapMain.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> coordinates = lineString.coordinates();
                    for (int i = 0; i < coordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)));
                    }
                    dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs(SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destOrigi1.longitude(), destOrigi1.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.marker)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }


    private void getRoute(final MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Log.e("TAG","Response code: " + response.code());
                if (response.body() == null) {
                    Log.e("TAG","No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("TAG","No routes found");
                    return;
                }
                drawNavigationPolylineRoute(response.body().routes().get(0));
                // Get the directions route


                // Make a toast which displays the route's distance


                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
                       /*     initSource(style);

                            initLayers(style)*/;
                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        return true;
    }
}