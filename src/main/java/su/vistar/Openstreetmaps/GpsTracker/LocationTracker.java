package su.vistar.Openstreetmaps.GpsTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LocationTracker {

    public static void main(String[] args) {
        try {
            String geoLocationData = sendRequest("https://ipapi.co/json/");

            JsonObject jsonObject = JsonParser.parseString(geoLocationData).getAsJsonObject();

            String city = jsonObject.get("city").getAsString();
            String region = jsonObject.get("region").getAsString();
            String country = jsonObject.get("country").getAsString();
            double latitude = jsonObject.get("latitude").getAsDouble();
            double longitude = jsonObject.get("longitude").getAsDouble();

            System.out.println("Город: " + city);
            System.out.println("Регион: " + region);
            System.out.println("Страна: " + country);
            System.out.println("Широта: " + latitude);
            System.out.println("Долгота: " + longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sendRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }
}
