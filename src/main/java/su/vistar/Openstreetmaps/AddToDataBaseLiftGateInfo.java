package su.vistar.Openstreetmaps;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.json.JSONArray;
import org.json.JSONObject;
import su.vistar.Openstreetmaps.models.LocalPlaceLiftGate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddToDataBaseLiftGateInfo {
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
        try {
            databaseUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> liftGates = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            System.out.println(element);

            JSONArray nodesArray = element.getJSONArray("nodes");
            long nodeId = nodesArray.getLong(0);
            Map<String, Double> coordinates = getNodeCoordinates(nodeId);

            Map<String, Object> liftGateInfo = new HashMap<>();
            liftGateInfo.put("id", id);

            liftGateInfo.put("coordinates", coordinates);
            liftGates.add(liftGateInfo);

            LocalPlaceLiftGate localPlaceLiftGate = new LocalPlaceLiftGate();
            localPlaceLiftGate.setGatesId(id);
            localPlaceLiftGate.setLat(coordinates.get("lon"));
            localPlaceLiftGate.setLon(coordinates.get("lat"));

            JSONObject tags = element.getJSONObject("tags");
            if (tags.has("name")) {
                localPlaceLiftGate.setName(tags.getString("name"));
            }
            persistDataBaseGate(localPlaceLiftGate);
        }
        return liftGates;
    }

    private static void persistDataBaseGate(LocalPlaceLiftGate localPlaceLiftGate) {
        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("br.com.fredericci.pu");
        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        LocalPlaceLiftGate entityLocalPlace = entityManager.find(LocalPlaceLiftGate.class, localPlaceLiftGate.getGatesId());

        entityManager.getTransaction().begin();
        if (entityLocalPlace == null) {
            entityManager.persist(localPlaceLiftGate);
        } else {
            entityManager.merge(localPlaceLiftGate);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    private static void databaseUpdate() throws LiquibaseException, SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/osm", "postgres", "postgres")) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new liquibase.Liquibase("db/changelog/master-changelog.xml", new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (SQLException | LiquibaseException e) {
            throw e;
        }
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
}

