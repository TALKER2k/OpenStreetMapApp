package su.vistar.Openstreetmaps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverpassExample {

    public static void main(String[] args) {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];way[\"barrier\"=\"lift_gate\"];out;";

        try {
            String response = sendOverpassQuery(overpassUrl, query);
            List<Map<String, Object>> liftGates = processOverpassResponse(response);
            System.out.println("Координаты шлагбаумов:");
            for (Map<String, Object> liftGate : liftGates) {
                System.out.println("ID: " + liftGate.get("id"));
                System.out.println("Координаты: " + liftGate.get("coordinates"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, Object>> processOverpassResponse(String response) throws Exception {
        List<Map<String, Object>> liftGates = new ArrayList<>();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");
            JSONArray nodesArray = element.getJSONArray("nodes");
            List<Map<String, Double>> coordinates = new ArrayList<>();
            for (int j = 0; j < nodesArray.length(); j++) {
                long nodeId = nodesArray.getLong(j);
                Map<String, Double> nodeCoordinates = getNodeCoordinates(nodeId);
                if (nodeCoordinates != null) {
                    coordinates.add(nodeCoordinates);
                }
            }
            Map<String, Object> liftGateInfo = new HashMap<>();
            liftGateInfo.put("id", id);
            liftGateInfo.put("coordinates", coordinates);
            liftGates.add(liftGateInfo);
            System.out.println(liftGates.get(i));
        }
        return liftGates;
    }

    private static Map<String, Double> getNodeCoordinates(long nodeId) throws Exception {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];node(" + nodeId + ");out;";

        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        if (elements.length() > 0) {
            JSONObject nodeElement = elements.getJSONObject(0);
            double lat = nodeElement.getDouble("lat");
            double lon = nodeElement.getDouble("lon");
            Map<String, Double> coordinates = new HashMap<>();
            coordinates.put("lat", lat);
            coordinates.put("lon", lon);
            return coordinates;
        }
        return null;
    }

    private static String sendOverpassQuery(String overpassUrl, String query) throws Exception {
        URL url = new URL(overpassUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        connection.getOutputStream().write(("data=" + query).getBytes("UTF-8"));

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
//    public static void main(String[] args) {
//        String overpassUrl = "https://overpass-api.de/api/interpreter";
//        String query = "[out:json];way[\"barrier\"=\"lift_gate\"];out;";
//
//        try {
//            // Отправка запроса к Overpass API
//            String response = sendOverpassQuery(overpassUrl, query);
//            System.out.println("Ответ от Overpass API:");
//            System.out.println(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String sendOverpassQuery(String overpassUrl, String query) throws Exception {
//        // Формирование URL для запроса
//        URL url = new URL(overpassUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        // Установка параметров запроса
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        connection.setDoOutput(true);
//
//        // Отправка запроса
//        connection.getOutputStream().write(("data=" + query).getBytes("UTF-8"));
//
//        // Получение и чтение ответа
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        StringBuilder response = new StringBuilder();
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            response.append(line);
//        }
//        reader.close();
//
//        // Закрытие соединения
//        connection.disconnect();
//
//        return response.toString();
//    }

//    public static void main(String[] args) {
//        String overpassUrl = "https://overpass-api.de/api/interpreter";
//        String query = "[out:json];way[\"barrier\"=\"swing_gate\"];out;";
//
//        try {
//            String response = sendOverpassQuery(overpassUrl, query);
//            System.out.println("Ответ от Overpass API:");
//            System.out.println(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String sendOverpassQuery(String overpassUrl, String query) throws Exception {
//        URL url = new URL(overpassUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        connection.setDoOutput(true);
//
//        connection.getOutputStream().write(("data=" + query).getBytes("UTF-8"));
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        StringBuilder response = new StringBuilder();
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            response.append(line);
//        }
//        reader.close();
//
//        connection.disconnect();
//
//        return response.toString();
//    }
}

