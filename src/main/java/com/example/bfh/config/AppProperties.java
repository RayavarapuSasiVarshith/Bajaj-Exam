package com.example.bfh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bfh")
public class AppProperties {
    private String name;
    private String regNo;
    private String email;
    private String baseUrl;
    private String generatePath;
    private String defaultSubmitPath;
    private String storePath;
    private boolean useBearerPrefix;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getGeneratePath() { return generatePath; }
    public void setGeneratePath(String generatePath) { this.generatePath = generatePath; }

    public String getDefaultSubmitPath() { return defaultSubmitPath; }
    public void setDefaultSubmitPath(String defaultSubmitPath) { this.defaultSubmitPath = defaultSubmitPath; }

    public String getStorePath() { return storePath; }
    public void setStorePath(String storePath) { this.storePath = storePath; }

    public boolean isUseBearerPrefix() { return useBearerPrefix; }
    public void setUseBearerPrefix(boolean useBearerPrefix) { this.useBearerPrefix = useBearerPrefix; }
}
