package com.example.spring_gpt.request;

import org.springframework.boot.SpringApplication;

import java.sql.Timestamp;

public class header {
    public String url="https://{sky-api.singularity-ai.com}/saas/api/v4/generate";
    public String app_key="60aae685650e3b5faf0c58902dd0510f";
    public String app_secret="7db458857a61a8101094e568f540e94c59fc11ffe9aa5385";
    public Timestamp timestamp =new Timestamp(System.currentTimeMillis());
    public String sign_content = app_key + app_secret + timestamp;
}
