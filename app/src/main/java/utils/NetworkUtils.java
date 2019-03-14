package utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;
import java.util.Calendar;

public class NetworkUtils implements LocationListener {
    final static String GITHUB_BASE_URL =
            "";
    final static String POWER_BASE_URL =
            "https://power.larc.nasa.gov/cgi-bin/v1/DataAccess.py?";

    final static String DARK_SKY_URL = "https://api.darksky.net/forecast/";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */

    final static String REQUEST = "request";
    final static String request_value = "execute";
    final static String IDENTIFIER = "identifier";
    final static String identifier_value = "SinglePoint";
    final static String PARAMETERS = "parameters";
    final static String parameter_value = "ALLSKY_SFC_SW_DWN,WS10M,PRECTOT";
    final static String STARTDATE = "startDate";
    final static String start_date_value = "";
    final static String ENDDATE = "endDate";
    final static String end_date_value = "";
    final static String LAT = "lat";
    final static String LON = "lon";
    final static String USERCOMM = "userCommunity";
    final static String user_comm_value = "SSE";
    final static String TEMPAV = "tempAverage";
    final static String tempav_value = "DAILY";
    final static String OLIST = "outputList";
    final static String olist_value = "JSON,ASCII";
    final static String USER = "user";
    final static String user_value = "anonymous";
    final static String API_KEY = "apikey";

    static LocationManager locationManager;
    String lat_value;
    String lon_value;
    static Context mContext;

    public NetworkUtils(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Builds the URL used to query GitHub.
     *
     * @param githubSearchQuery The keyword that will be queried for.
     * @return The URL to use to query the GitHub.
     */
    public URL buildUrl() {
        Location dloc = getCoordinates();
        onLocationChanged(dloc);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        String sDate = dateFormat.format(cal.getTime());
        Log.e("SDATE", sDate);
        String eDate = dateFormat.format(cal.getTime());
        Log.e("EDATE", eDate);
        String fullUri = DARK_SKY_URL + API_KEY + "/" + lat_value + "/" + lon_value;

//        Uri builtUri = Uri.parse(POWER_BASE_URL).buildUpon()
//                .appendQueryParameter(REQUEST, request_value)
//                .appendQueryParameter(IDENTIFIER, identifier_value)
//                .appendQueryParameter(PARAMETERS, parameter_value)
//                .appendQueryParameter(STARTDATE, eDate)
//                .appendQueryParameter(ENDDATE, sDate)
//                .appendQueryParameter(LAT, lat_value)
//                .appendQueryParameter(LON, lon_value)
//                .appendQueryParameter(USERCOMM, user_comm_value)
//                .appendQueryParameter(TEMPAV, tempav_value)
//                .appendQueryParameter(OLIST, olist_value)
//                .appendQueryParameter(USER, user_value)
//                .build();
        Uri builtUriDark = Uri.parse(fullUri).buildUpon().build();


        URL url = null;
        try {
            url = new URL(builtUriDark.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Location getCoordinates() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return location;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        lon_value = Double.toString(longitude);
        lat_value = Double.toString(latitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
