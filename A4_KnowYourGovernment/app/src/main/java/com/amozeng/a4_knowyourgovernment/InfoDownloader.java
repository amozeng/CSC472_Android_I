package com.amozeng.a4_knowyourgovernment;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class InfoDownloader implements Runnable {

    private static final String TAG = "InfoDownloader";
    private static final String URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBd5NzTx5KI8JLZm3RgJTnktiu-iL34cFY&address=";
    private static String location;
    private MainActivity mainActivity;

    public static HashMap<String, String> officialMap = new HashMap<>();

    public InfoDownloader(MainActivity m, String loc){
        this.mainActivity = m;
        this.location = loc;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(URL + location).buildUpon();
        String urlToUse = uriBuilder.toString();

        Log.d(TAG, "run: "+ urlToUse);

        StringBuilder stringBuilder = new StringBuilder();

        try{
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d(TAG, "run: HTTP ResponseCode NOT OK" + connection.getResponseCode());
                return;
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while( (line = reader.readLine()) != null ) {
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "run: "+ stringBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        process(stringBuilder.toString());
    }

    private void process(String s){
        try{
            JSONObject jOfficials = new JSONObject(s);

            // PART I: normalizedInput
            JSONObject normalizedInput = jOfficials.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");

            Office newOffice;

            // PART II: offices
            JSONArray offices = jOfficials.getJSONArray("offices");

            //
            JSONArray officials =  jOfficials.getJSONArray("officials");

            for(int i = 0; i < offices.length(); i++){

                JSONObject office = (JSONObject) offices.get(i);
                final String officeName = office.getString("name");
                JSONArray indices =  office.getJSONArray("officialIndices");
                for(int j = 0; j < indices.length(); j++){
                    int index = Integer.parseInt(indices.get(j).toString());

                    // PART III: officials
                    JSONObject details = (JSONObject) officials.get(index);
                    final String personName = details.getString("name");

                    newOffice = new Office(officeName, personName);


                    // address part
                    if(details.has("address")){
                        JSONArray address = details.getJSONArray("address");
                        JSONObject theAddr = (JSONObject) address.get(0);
                        String line1 = "", line2 = "", Addr_City = "", Addr_State = "", Addr_Zip = "";
                        if(theAddr.has("line1")){ line1 = theAddr.getString("line1");}
                        if(theAddr.has("line2")) { line2 = theAddr.getString("line2");}
                        if(theAddr.has("city")) { Addr_City = theAddr.getString("city");}
                        if(theAddr.has("state")) {Addr_State = theAddr.getString("state");}
                        if(theAddr.has("zip")) {Addr_Zip = theAddr.getString("zip");}

                        String line_1 = line1 + line2;
                        String line_2 = Addr_City + ", " + Addr_State + " " + Addr_Zip;
                        //String fullAddress = line_1 + "\n" + line_2;
                        String fullAddress = line_1 + ", " + line_2;

                        newOffice.setAddress(fullAddress);
                    }

                    if(details.has("party")){
                        String party = details.getString("party");
                        newOffice.setParty(party);
                    }

                    if(details.has("phones")){
                        JSONArray phoneArray = details.getJSONArray("phones");
                        String phone = (String) phoneArray.get(0);
                        newOffice.setPhone(phone);
                    }

                    if(details.has("urls")){
                        JSONArray urlArray = details.getJSONArray("urls");
                        String url = (String) urlArray.get(0);
                        newOffice.setWebsite(url);
                    }


                    if(details.has("emails")){
                        JSONArray emailArray = details.getJSONArray("emails");
                        String email = (String) emailArray.get(0);
                        newOffice.setEmail(email);
                    }

                    if(details.has("photoUrl")){
                        String photoUrl = details.getString("photoUrl");
                        newOffice.setPhotoUrls(photoUrl);
                    }

                    if(details.has("channels")){
                        JSONArray channelArray = details.getJSONArray("channels");
                        for(int k = 0; k < channelArray.length(); k++){
                            JSONObject channel = (JSONObject) channelArray.get(k);
                            String channelType = channel.getString("type");
                            String channelId = channel.getString("id");
                            switch (channelType){
                                case "Facebook":
                                    newOffice.setFacebookID(channelId);
                                    break;
                                case "Twitter":
                                    newOffice.setTwitterID(channelId);
                                    break;
                                case "YouTube":
                                    newOffice.setYoutubeID(channelId);
                                    break;
                                default:
                                    Log.d(TAG, "process: no channel" );
                            }
                        }
                    }

                    final Office finalNewOffice = newOffice;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.addOffice(finalNewOffice);
                            Log.d(TAG, "run: finished adding:  " + officeName + " " + personName);
                        }
                    });

                }

            }




        }catch(Exception e){
            Log.d(TAG, "process: " + e.getMessage());
        }
    }
}
