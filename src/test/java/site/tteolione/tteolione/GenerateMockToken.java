package site.tteolione.tteolione;


import org.springframework.http.HttpHeaders;

public abstract class GenerateMockToken {

    public static HttpHeaders getToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = "Bearer access_token";
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        return httpHeaders;
    }
}
