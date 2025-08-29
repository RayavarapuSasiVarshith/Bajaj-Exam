package com.example.bfh.service;

import com.example.bfh.config.AppProperties;
import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.dto.GenerateWebhookResponse;
import com.example.bfh.service.storage.ResultStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final AppProperties props;
    private final WebhookClient client;
    private final SqlSolver solver;
    private final ResultStorage storage;

    public StartupRunner(AppProperties props, WebhookClient client, SqlSolver solver, ResultStorage storage) {
        this.props = props;
        this.client = client;
        this.solver = solver;
        this.storage = storage;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("BFH Java Qualifier app starting with regNo={}", props.getRegNo());

        // 1) Call generateWebhook
        GenerateWebhookRequest req = new GenerateWebhookRequest(props.getName(), props.getRegNo(), props.getEmail());
        GenerateWebhookResponse resp = client.generateWebhook(req);
        if (resp == null || resp.getAccessToken() == null) {
            log.error("Failed to get accessToken/webhook. Aborting.");
            return;
        }
        String webhook = resp.getWebhook();
        String token = resp.getAccessToken();
        log.info("Received webhook='{}'", webhook);

        // 2) Decide question based on last two digits of regNo
        boolean isOdd = isLastTwoDigitsOdd(props.getRegNo());
        log.info("Assigned question: {}", isOdd ? "Question 1 (odd)" : "Question 2 (even)");

        // 3) Solve SQL
        String finalQuery = solver.solve(isOdd);
        storage.saveFinalQuery(finalQuery);

        // 4) Submit final SQL query to webhook (fallback to default path from spec if webhook is missing)
        String submitTarget = (webhook != null && !webhook.isBlank()) ? webhook : props.getDefaultSubmitPath();
        boolean ok = client.submitFinalQuery(submitTarget, token, finalQuery);
        if (ok) {
            log.info("Final query submitted successfully.");
        } else {
            log.error("Final query submission failed.");
        }
    }

    private boolean isLastTwoDigitsOdd(String regNo) {
        if (regNo == null) return false;
        String digits = regNo.replaceAll("\D+", "");
        if (digits.length() < 2) {
            try {
                int val = Integer.parseInt(digits);
                return (val % 2) == 1;
            } catch (Exception e) {
                return false;
            }
        }
        int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        return (lastTwo % 2) == 1;
    }
}
