package com.example.spring_gpt;

import org.apache.hc.core5.http.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Scanner;

@SpringBootApplication
public class SpringGptApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpringGptApplication.class, args);
        System.out.println("请输入你想要询问的问题:");
        String content;
        Scanner scanner=new Scanner((System.in));
        while(true) {
            content=scanner.nextLine();
            generateStream(content,false);
        }
    }
    public static void generateStream(String content,boolean stream) throws IOException {
        String apiUrl = "https://sky-api.singularity-ai.com/saas/api/v4/generate";
        String appKey = "60aae685650e3b5faf0c58902dd0510f";
        String appSecret = "7db458857a61a8101094e568f540e94c59fc11ffe9aa5385";
        String payload = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + content + "\"}],\"model\":\"SkyChat-MegaVerse\"}";
        URL url=new URL(apiUrl);
        HttpURLConnection  connection=(HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("app_key", appKey);
        connection.setRequestProperty("timestamp", String.valueOf(Instant.now().getEpochSecond()));
        connection.setRequestProperty("sign", calculateSignature(appKey, appSecret));
        connection.setRequestProperty("content-type", "application/json");
        if(stream){
            connection.setRequestProperty("stream", "true");
        }
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            System.out.println(payload);
            os.write(input, 0, input.length);
        }

        // Read the response
        if (stream) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println(response.toString());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close the connection
        connection.disconnect();
    }


    public static String calculateSignature(String appKey, String appSecret) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String toBeHashed = appKey + appSecret + timestamp;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(toBeHashed.getBytes(StandardCharsets.UTF_8));
            // Convert MD5 hash to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // Truncate to first 16 characters (32 bits)
            return hexString.substring(0, 32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
