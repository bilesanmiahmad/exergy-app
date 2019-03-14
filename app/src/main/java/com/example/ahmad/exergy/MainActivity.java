package com.example.ahmad.exergy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    TextView txtCordinates;
    TextView txtSearchUrl;
    TextView txtSearchResult;
    TextView txtInsoToday;
    TextView txtPrecToday;
    TextView txtWsToday;
    ProgressBar pb_data_loader;
    LocationManager locationManager;
    NetworkUtils networkUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCordinates = findViewById(R.id.tv_cordinates);
        txtSearchUrl = findViewById(R.id.tv_search_url);
        txtSearchResult = findViewById(R.id.tv_search_results);
        txtInsoToday = findViewById(R.id.tv_inso_today);
        txtPrecToday = findViewById(R.id.tv_prec_today);
        txtWsToday = findViewById(R.id.tv_ws_today);
        pb_data_loader = findViewById(R.id.pgb_data);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        networkUtils = new NetworkUtils(this);

    }

    private void makePowerSearchQuery(){
        URL powerSearchURL = networkUtils.buildUrl();
        txtSearchUrl.setText(powerSearchURL.toString());
        new POWERQueryTask().execute(powerSearchURL);
    }

    public class POWERQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            pb_data_loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String powerSearchResults = null;
            try{
                powerSearchResults = networkUtils.getResponseFromHttpUrl(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return powerSearchResults;
        }

        @Override
        protected void onPostExecute(String powerSearchResults) {
            pb_data_loader.setVisibility(View.INVISIBLE);
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            String sDate = dateFormat.format(cal.getTime());

            String today = dateFormat.format(date);
            if (powerSearchResults !=null && !powerSearchResults.equals("")){
                txtSearchResult.setText(powerSearchResults);
//                try {
//                    JSONObject reader = new JSONObject(powerSearchResults);
//                    JSONArray features = reader.getJSONArray("features");
//                    JSONObject feature = features.getJSONObject(0);
//                    JSONObject properties = feature.getJSONObject("properties");
//                    JSONObject parameter = properties.getJSONObject("parameter");
//                    JSONObject insolation = parameter.getJSONObject("ALLSKY_SFC_SW_DWN");
//                    JSONObject precipitation = parameter.getJSONObject("PRECTOT");
//                    JSONObject wind_speed = parameter.getJSONObject("WS10M");
//
//                    String inso_today = insolation.getString(sDate);
//                    txtInsoToday.setText(inso_today);
//                    Log.e("INSOLATION", inso_today);
//
//                    String prec_today = precipitation.getString(sDate);
//                    txtPrecToday.setText(prec_today);
//                    Log.e("PREC", prec_today);
//
//                    String ws_today = wind_speed.getString(sDate);
//                    txtWsToday.setText(ws_today);
//                    Log.e("WS", ws_today);

//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
            }
            else{
                txtSearchResult.setText("An Error occured");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.action_get_data){
            makePowerSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
