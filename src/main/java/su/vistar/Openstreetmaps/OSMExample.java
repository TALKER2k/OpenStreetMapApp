package su.vistar.Openstreetmaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OSMExample {

    public static void main(String[] args) {
        String apiUrl = "https://overpass-api.de/api/interpreter?barrier=swing_gate";

        try {
            // Формируем URL для запроса
            URL url = new URL(apiUrl);

            // Открываем соединение
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Устанавливаем метод запроса
            connection.setRequestMethod("POST");

            // Получаем ответ от сервера
            int responseCode = connection.getResponseCode();

            // Если ответ успешный (код 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем ответ
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Обработка полученных данных
                System.out.println("Ответ от сервера:");
                System.out.println(response.toString());
            } else {
                System.out.println("Ошибка при выполнении запроса. Код ответа: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}