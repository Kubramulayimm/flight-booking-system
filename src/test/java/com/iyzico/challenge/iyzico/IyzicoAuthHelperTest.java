package com.iyzico.challenge.iyzico;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class IyzicoAuthHelperTest {

    private final IyzicoAuthHelper helper = new IyzicoAuthHelper();

    @Test
    void buildAuthorizationHeader_shouldGenerateValidHeader() {
        // given
        String apiKey = "test-api-key";
        String secretKey = "secret";
        String randomKey = "123456";
        String uriPath = "/payment/auth";
        String requestBodyJson = "{\"price\":\"100.0\"}";

        // when
        String header = helper.buildAuthorizationHeader(
                apiKey,
                secretKey,
                randomKey,
                uriPath,
                requestBodyJson
        );

        // then
        assertThat(header).startsWith("IYZWSv2 ");

        String base64Part = header.substring("IYZWSv2 ".length());
        String decoded = new String(Base64.getDecoder().decode(base64Part), StandardCharsets.UTF_8);

        assertThat(decoded).contains("apiKey:" + apiKey);
        assertThat(decoded).contains("randomKey:" + randomKey);
        assertThat(decoded).contains("signature:");

        String signature = decoded.substring(decoded.indexOf("signature:") + "signature:".length());
        assertThat(signature).hasSize(64);
        assertThat(signature).matches("[0-9a-f]+");
    }

    @Test
    void buildAuthorizationHeader_shouldWorkWhenRequestBodyIsNull() {
        // given
        String apiKey = "api";
        String secretKey = "secret";
        String randomKey = "rk";
        String uriPath = "/payment";
        String requestBodyJson = null;

        // when
        String header = helper.buildAuthorizationHeader(
                apiKey,
                secretKey,
                randomKey,
                uriPath,
                requestBodyJson
        );

        // then
        assertThat(header).startsWith("IYZWSv2 ");

        String decoded = new String(
                Base64.getDecoder().decode(header.replace("IYZWSv2 ", "")),
                StandardCharsets.UTF_8
        );

        assertThat(decoded).contains("apiKey:" + apiKey);
        assertThat(decoded).contains("randomKey:" + randomKey);
        assertThat(decoded).contains("signature:");
    }
}
