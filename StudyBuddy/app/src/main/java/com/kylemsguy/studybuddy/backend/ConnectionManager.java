package com.kylemsguy.studybuddy.backend;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kyle on 01/02/15.
 */
public class ConnectionManager {
    private final String USER_AGENT = "Mozilla/5.0";

    private HttpURLConnection connection;

    private static final boolean BACKEND_DEBUG = false;

    private static final String BASE_URL = "http://104.236.221.152/sb/";

    public ConnectionManager (){
        // TODO nothing
    }

    public String register(String username, String email, String gcm_id) throws Exception {

        String postParams;
        JSONObject paramData = new JSONObject();

        try {
            paramData.put("user_name", username);
            paramData.put("user_email", email);
            paramData.put("reg_id", gcm_id);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        postParams = paramData.getString();

        // Send data
        return sendPost(BASE_URL + "register_user/", postParams);

    }

    public void updateCoords(String usr_id, double latitude, double longitude) throws Exception {
        JSONObject paramData = new JSONObject();

        try {
            paramData.put("user_id", usr_id);
            paramData.put("lat", latitude);
            paramData.put("long", longitude);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sendPost(BASE_URL + "register_user/", paramData.toString());

    }

    public void addCourses(String usr_id, String courses_str) throws Exception {
        JSONObject paramData = new JSONObject();

        try {
            paramData.put("user_id", usr_id);
            paramData.put("courses_str", courses_str);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sendPost(BASE_URL + "register_user/", paramData.toString());
    }

    public void getNearbyUsers(String[] courses, double latitude, double longitude, int radius) throws Exception {
        StringBuilder sb = new StringBuilder();
        for(String course: courses){
            sb.append(course);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        String url = BASE_URL + "close_users" + sb.toString() + "/" + Double.toString(latitude) +
                "/" + Double.toString(longitude) + "/" + Integer.toString(radius) + "/";

        String response = sendGet(url);

        JSONObject respJson = new JSONObject(response);

        List<>

    }

    // HTTP GET request
    public String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        connection = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    /**
     * Sends a POST request to the url with a String as the parameters. The
     * parameters must have been pre-formatted beforehand.
     *
     * @param url        The URL to send the POST request to
     * @param postParams The parameters to be sent. This will be sent as-is.
     * @return
     * @throws Exception
     */
    public String sendPost(String url, String postParams) throws Exception {
        // start the connection
        URL obj = new URL(url);
        connection = (HttpURLConnection) obj.openConnection();

        // now time to act like a browser
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection
                .setRequestProperty("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setFixedLengthStreamingMode(postParams.getBytes().length);
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        // COOKIES
        for (String cookie : this.cookies) {
            connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        connection.setRequestProperty("Host", "twocansandstring.com");

        connection.setRequestProperty("User-Agent", USER_AGENT);

        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        boolean redirect = false;

        // normally, 3xx is redirect
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true;
        }

        if (BACKEND_DEBUG) {
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + postParams);
            System.out.println("Response Code ... " + status);
        }
        if (redirect) {

            // get redirect url from "location" header field
            String newUrl = connection.getHeaderField("Location");

            // get the cookie if need, for login
            String cookies = connection.getHeaderField("Set-Cookie");

            // open the new connnection again
            connection = (HttpURLConnection) new URL(newUrl).openConnection();
            connection.setRequestProperty("Cookie", cookies);
            connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            connection.addRequestProperty("User-Agent", "Mozilla");
            connection.addRequestProperty("Referer", "google.com");

            if (BACKEND_DEBUG)
                System.out.println("Redirect to URL : " + newUrl);

        }

        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
