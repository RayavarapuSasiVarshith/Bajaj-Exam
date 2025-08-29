package com.example.bfh.service;

import com.example.bfh.config.AppProperties;
import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebhookClient {
    private static final Logger log = LoggerFactory.getLogger(WebhookClient.class);

    private final WebClient webClient;
    private final AppProperties props;

    public WebhookClient(AppProperties props) {
        this.props = props;
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    public GenerateWebhookResponse generateWebhook(GenerateWebhookRequest request) {
        String path = props.getGeneratePath();
        log.info("Calling generateWebhook at {}{}", props.getBaseUrl(), path);

        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class)
                .onErrorResume(ex -> {
                    log.error("Error calling generateWebhook: {}", ex.getMessage());
                    return Mono.empty();
                })
                .block();
    }

    public boolean submitFinalQuery(String submitUrlOrPath, String accessToken, String finalQuery) {
        String url = submitUrlOrPath.startsWith("http") ? submitUrlOrPath : props.getBaseUrl() + submitUrlOrPath;

        HttpHeaders headers = new HttpHeaders();
        String token = accessToken;
        if (props.isUseBearerPrefix() && accessToken != null && !accessToken.startsWith("Bearer ")) {
            token = "Bearer " + accessToken;
        }
        headers.set(HttpHeaders.AUTHORIZATION, token);
        log.info("Submitting finalQuery to {}", url);

        var payload = new java.util.HashMap<String, String>();
        payload.put("finalQuery", finalQuery);

        try {
            String resp = webClient.post()
                    .uri(url)
                    .headers(h -> h.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Submission response: {}", resp);
            return true;
        } catch (Exception ex) {
            log.error("Error submitting finalQuery: {}", ex.toString());
            return false;
        }
    }
}
