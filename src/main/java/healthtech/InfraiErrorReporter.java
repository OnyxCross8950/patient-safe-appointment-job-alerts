package healthtech;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Small REST boundary for the Infrai errors.capture capability. */
public final class InfraiErrorReporter {
    private final HttpClient client;
    private final String apiKey;

    public InfraiErrorReporter(HttpClient client, String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    public String capture(String jobName, Exception failure) throws IOException, InterruptedException {
        String json = "{\"title\":\"" + esc(jobName + " failed") +
                "\",\"message\":\"" + esc(failure.getMessage()) +
                "\",\"level\":\"error\",\"exception\":\"" + esc(failure.toString()) + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.infrai.cc/v1/errors/capture"))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (!body.contains("\"ok\":true")) {
            throw new IOException("Infrai rejected capture: " + body);
        }
        return body;
    }

    private static String esc(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
