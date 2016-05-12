//Here's the weather map example again. This time we use AsyncTask

package com.course.example.asynctaskexample;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private ProgressDialog progDailog;
    private String APPID = "1937c3565d027796ab90ecade26ee182";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
    }

    //when button is clicked, background thread is started
    public void open(View view){

        String queryString = editText.getText().toString() + "&APPID=" + APPID;
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + queryString;

        new GetWeatherTask(textView, editText).execute(url);
    }

    //----------------------------------------------------------
    //AsyncTask inner class
    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        private TextView textView;
        private EditText editText;
        private String location;

        public GetWeatherTask(TextView textView, EditText editText) {
            this.textView = textView;
            this.editText = editText;
            location = editText.getText().toString();
        }

        //start dialog widget
        @Override
        protected void onPreExecute(){
            progDailog = ProgressDialog.show(MainActivity.this, "AsyncTask Demo",
                    "Working....", true);
        }

        //runs on background thread
        @Override
        protected String doInBackground(String... strings) {
            String weather = "UNDEFINED";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                //get temperature from JSON
                JSONObject topLevel = new JSONObject(builder.toString());
                JSONObject main = topLevel.getJSONObject("main");
                weather = String.valueOf(main.getDouble("temp"));

                //convert to Farenheit
                float temp = Float.parseFloat(weather) - 273.15f;
                temp = temp*(9.0f/5.0f) + 32f;
                weather = String.format("%s TEMP is  %.2f F",location , temp);

                urlConnection.disconnect();

                //give the progress dialog a chance to show off
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {}

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        //takes return of background thread and places it on UI
        @Override
        protected void onPostExecute(String weather) {
            progDailog.dismiss();
            textView.setText(weather);
            editText.setText("");
        }
    }

}
