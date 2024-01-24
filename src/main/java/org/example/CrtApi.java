package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrtApi {
    private final TimeUnit timeUnit;
    private final HttpClient httpClient;
    private final Semaphore requestSemaphore;
    private final ExecutorService executorService;
    private final int requestLimit;
    private final Runnable request;
    private int counterRequest = 0;
    private long startPeriod;

    public CrtApi(TimeUnit timeUnit,
                  int requestLimit,
                  int nThreads,
                  String accessToken,
                  Document document,
                  String signature) {
        this.timeUnit = timeUnit;
        this.httpClient = HttpClient.newHttpClient();
        this.requestLimit = requestLimit;
        this.requestSemaphore = new Semaphore(requestLimit);
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.request = () -> createDocument(accessToken, document, signature);
    }



    public void sendRequest() {
        executorService.execute(request);
    }

    public void close() {
        executorService.close();
    }
    private void createDocument(String accessToken, Document document, String signature) {
        try {
            requestSemaphore.acquire();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.valueToTree(document);
            requestBody.put("signature", signature);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Document created successfully");
            } else {
                System.out.println("Failed to create document. Status code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            requestSemaphore.release();
        }
    }
}
