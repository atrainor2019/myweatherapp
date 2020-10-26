package com.example.myapplication;

//imports
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecondaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    //initialize variables to assign by element ID and display data
    String API_KEY = "4eba6e69b643edd0fca09316822bd45e";

    //initialize return button
    private Button goBackbutton;
    //initialize google map
    private GoogleMap mMap;

    private RequestQueue mQueue;

    //initialize the variables
    TextView temperature, temphigh, templow, humidity, city, sunrise, sunset, latitude, longitude, pressure, windspeed;
    int templowF, temphighF, temp;
    double lat, lon;



    //create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        //get the intent
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        //initialize elements by ID
        humidity = findViewById(R.id.humidity);
        temperature = findViewById(R.id.temperature);
        temphigh = findViewById(R.id.temphigh);
        templow = findViewById(R.id.templow);
        city = findViewById(R.id.city);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        pressure = findViewById(R.id.pressure);
        windspeed = findViewById(R.id.windspeed);

        //setup new request queue using volley
        mQueue = Volley.newRequestQueue(this);

        //parse the JSON data according to the user input
        jsonParse(userinput);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //grab the go back button using ID
        goBackbutton = (Button) findViewById(R.id.newsearch);

        //set onclick listener for the button to return the the previous page to search again
        goBackbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //finish the activity and return to previous
                finish();
            }
        });
    }

    //check whether the user has input a City, a Zipcode, or a GPS string structured as (lat,lon)
    private String getCityZipGps(String userinput){
        try{
           int yourNumber = Integer.parseInt(userinput);
           String zip = "https://api.openweathermap.org/data/2.5/weather?zip=" + userinput;
           return zip;
        }catch (NumberFormatException ex) {

            if(userinput.indexOf(',') != -1){
                double latitude, longitude;
                String[] numberArray;
                numberArray = userinput.split(",");

                latitude = Double.parseDouble(numberArray[0]);
                longitude = Double.parseDouble(numberArray[1]);

                String latlong ="https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude;

                return latlong;

            }
            String city = "https://api.openweathermap.org/data/2.5/weather?q=" + userinput;
            return city;
        }

    }

    //Parsing the JSON data for userinput string.
    private void jsonParse(String userinput){

        //get the url which we want to retrieve JSON Data
        String url = getCityZipGps(userinput) + "&APPID=" + API_KEY;

        //setup a new request and Parse Data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {//set the TextView element resulting values in the application for the data that is retrieved.
                            humidity.setText(response.getJSONObject("main").getInt("humidity") + "%");
                            templowF = (response.getJSONObject("main").getInt("temp_min") - 273) * 9/5 + 32;
                            temphighF = (response.getJSONObject("main").getInt("temp_max") - 273) * 9/5 + 32;
                            temp = (response.getJSONObject("main").getInt("temp") - 273) * 9/5 + 32;
                            templow.setText(templowF + "°");
                            temphigh.setText(temphighF + "°");
                            temperature.setText(temp + "°");
                            city.setText(response.getString("name"));

                            lat = response.getJSONObject("coord").getDouble("lat");
                            lon = response.getJSONObject("coord").getDouble("lon");

                            sunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date((response.getJSONObject("sys").getLong("sunrise")) * 1000)));
                            sunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date((response.getJSONObject("sys").getLong("sunset")) * 1000)));

                            pressure.setText(response.getJSONObject("main").getInt("pressure") + " hPa");

                            latitude.setText(lat + "");
                            longitude.setText(lon + "");

                            windspeed.setText(response.getJSONObject("wind").getInt("speed") + " mph");

                            //call the newLocation() method to update the map according to the coordinates for the input
                            newLocation();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //add the request using volley.
        mQueue.add(request);
    }

    //set map on ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pos = new LatLng(lat, lon);

        mMap.addMarker(new
                MarkerOptions().position(pos).title("Florida Atlantic University"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    //update the location of the map
    void newLocation() {
        if (mMap != null) {
            LatLng pos = new LatLng(lat, lon); //get new position
            mMap.addMarker(new
                    MarkerOptions().position(pos).title("Andrew Trainor Weather App"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 11)); // move camera and change zoom, all other properties remain the same

        }
    }

}
