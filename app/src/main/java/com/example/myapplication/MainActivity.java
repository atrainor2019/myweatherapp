package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String USER_INPUT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //called when the user taps the search button and sends the info to the second activity
    public void sendWeatherInfo(View view){
        Intent intent = new Intent(this, SecondaryActivity.class);
        EditText editText = (EditText) findViewById(R.id.weatherInput);

        //get the userinput
        String userInput = editText.getText().toString();

        //pass the userinput data to the next activity
        intent.putExtra(USER_INPUT, userInput);

        //move to the next activity
        startActivity(intent);
    }


}