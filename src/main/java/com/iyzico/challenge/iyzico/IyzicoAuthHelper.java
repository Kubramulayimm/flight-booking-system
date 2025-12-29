package com.iyzico.challenge.iyzico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class IyzicoAuthHelper {

    public String buildAuthorizationHeader(String apiKey, String secretKey, String randomKey, String uriPath, String requestBodyJson) {

        String payload = randomKey + uriPath + (requestBodyJson == null ? "" : requestBodyJson);

        String signatureHex = hmacSha256Hex(payload, secretKey);

        String authorizationString = "apiKey:" + apiKey
                + "&randomKey:" + randomKey
                + "&signature:" + signatureHex;

        String base64Encoded = Base64.getEncoder().encodeToString(authorizationString.getBytes(StandardCharsets.UTF_8));
        return "IYZWSv2 " + base64Encoded;
    }

    private String hmacSha256Hex(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Iyzico signature", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
