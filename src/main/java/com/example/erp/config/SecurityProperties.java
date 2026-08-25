package com.example.erp.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private final Jwt jwt = new Jwt();
    private final Cookie cookie = new Cookie();
    private final Bootstrap bootstrap = new Bootstrap();
    private final Password password = new Password();
    public Jwt getJwt() { return jwt; }
    public Cookie getCookie() { return cookie; }
    public Bootstrap getBootstrap() { return bootstrap; }
    public Password getPassword() { return password; }
    public static class Jwt {
        private String secret;
        private Duration accessTtl = Duration.ofMinutes(15);
        private Duration refreshTtl = Duration.ofDays(30);
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public Duration getAccessTtl() { return accessTtl; }
        public void setAccessTtl(Duration value) { accessTtl = value; }
        public Duration getRefreshTtl() { return refreshTtl; }
        public void setRefreshTtl(Duration value) { refreshTtl = value; }
    }
    public static class Cookie {
        private String accessName = "erp_access";
        private String refreshName = "erp_refresh";
        private boolean secure = true;
        private String sameSite = "Strict";
        private String path = "/";
        private String domain = "";
        public String getAccessName() { return accessName; }
        public void setAccessName(String value) { accessName = value; }
        public String getRefreshName() { return refreshName; }
        public void setRefreshName(String value) { refreshName = value; }
        public boolean isSecure() { return secure; }
        public void setSecure(boolean value) { secure = value; }
        public String getSameSite() { return sameSite; }
        public void setSameSite(String value) { sameSite = value; }
        public String getPath() { return path; }
        public void setPath(String value) { path = value; }
        public String getDomain() { return domain; }
        public void setDomain(String value) { domain = value; }
    }
    public static class Bootstrap {
        private boolean enabled = true;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean value) { enabled = value; }
    }
    public static class Password {
        private int saltLength = 16;
        private int hashLength = 32;
        private int parallelism = 1;
        private int memory = 65536;
        private int iterations = 3;
        public int getSaltLength() { return saltLength; }
        public void setSaltLength(int value) { saltLength = value; }
        public int getHashLength() { return hashLength; }
        public void setHashLength(int value) { hashLength = value; }
        public int getParallelism() { return parallelism; }
        public void setParallelism(int value) { parallelism = value; }
        public int getMemory() { return memory; }
        public void setMemory(int value) { memory = value; }
        public int getIterations() { return iterations; }
        public void setIterations(int value) { iterations = value; }
    }
}