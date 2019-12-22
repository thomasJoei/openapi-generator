package org.openapitools.client;

import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.openapitools.client.auth.Authentication;
import org.openapitools.client.auth.HttpBasicAuth;
import org.openapitools.client.auth.HttpBearerAuth;
import org.openapitools.client.auth.ApiKeyAuth;
import org.openapitools.client.auth.OAuth;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class ApiClientBuilder {

    private final boolean debugging;
    private final HttpHeaders defaultHeaders;
    private final MultiValueMap<String, String> defaultCookies;
    private final String basePath;
    private final RestTemplate restTemplate;
    private final Map<String, Authentication> authentications;
    private final DateFormat dateFormat;


    /**
     * Default configuration
     */
    public ApiClientBuilder() {
        String basePath = "http://petstore.swagger.io:80/v2";
        // Use RFC3339 format for date and datetime.
        // See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
        DateFormat dateFormat = new RFC3339DateFormat();

        // Use UTC as the default time zone.
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Set default User-Agent.
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("User-Agent", "Java-SDK");

        // Setup authentications (key: authentication name, value: authentication).
        Map<String, Authentication> authentications = new HashMap<String, Authentication>();
        authentications.put("api_key", new ApiKeyAuth("header", "api_key"));
        authentications.put("api_key_query", new ApiKeyAuth("query", "api_key_query"));
        authentications.put("http_basic_test", new HttpBasicAuth());
        authentications.put("petstore_auth", new OAuth());

        // Prevent the authentications from being modified.
        authentications = Collections.unmodifiableMap(authentications);

        this.basePath = basePath;
        this.restTemplate = ApiClient.buildRestTemplate();
        this.authentications = authentications;
        this.defaultHeaders = defaultHeaders;
        this.defaultCookies = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, String>());
        this.dateFormat = dateFormat;
        this.debugging = false;
    };

    private ApiClientBuilder(String basePath, RestTemplate restTemplate, Map<String, Authentication> authentications, HttpHeaders defaultHeaders, MultiValueMap<String, String> defaultCookies, DateFormat dateFormat, boolean debugging) {
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.authentications = Collections.unmodifiableMap(authentications);
        this.defaultHeaders = defaultHeaders;
        this.defaultCookies = CollectionUtils.unmodifiableMultiValueMap(defaultCookies);
        this.dateFormat = dateFormat;
        this.debugging = debugging;
    }

    public ApiClientBuilder basePath(String basePath) {
        return new ApiClientBuilder(basePath, this.restTemplate, this.authentications, this.defaultHeaders, this.defaultCookies, this.dateFormat, this.debugging);
    }

    public ApiClientBuilder restTemplate(RestTemplate restTemplate) {
        return new ApiClientBuilder(this.basePath, restTemplate, this.authentications, this.defaultHeaders, this.defaultCookies, this.dateFormat, this.debugging);
    }

    public ApiClientBuilder authentications(Map<String, Authentication> authentications) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, authentications, this.defaultHeaders, this.defaultCookies, this.dateFormat, this.debugging);
    }

    public ApiClientBuilder defaultHeaders(HttpHeaders defaultHeaders) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, this.authentications, defaultHeaders, this.defaultCookies, this.dateFormat, this.debugging);
    }

    public ApiClientBuilder defaultCookies(MultiValueMap<String, String> defaultCookies) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, this.authentications, this.defaultHeaders, defaultCookies, this.dateFormat, this.debugging);
    }

    public ApiClientBuilder dateFormat(DateFormat dateFormat) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, this.authentications, this.defaultHeaders, this.defaultCookies, dateFormat, this.debugging);
    }

    public ApiClientBuilder basicAuth(HttpBasicAuth httpBasicAuth) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, replaceAuthentication(this.authentications, httpBasicAuth), this.defaultHeaders, this.defaultCookies, this.dateFormat, debugging);
    }

    public ApiClientBuilder debugging(boolean debugging) {
        return new ApiClientBuilder(this.basePath, this.restTemplate, this.authentications, this.defaultHeaders, this.defaultCookies, this.dateFormat, debugging);
    }


    public ApiClient build() {
        return new ApiClient(this.basePath, this.restTemplate, this.authentications, this.defaultHeaders, this.defaultCookies, this.dateFormat, this.debugging);
    }

    private Map<String, Authentication> replaceAuthentication(Map<String, Authentication> auths, Authentication newAuthentication) {
        Map<String, Authentication> authentications = new HashMap<>(auths);

        for (Map.Entry<String, Authentication> entry : authentications.entrySet()) {
            Authentication auth = entry.getValue();

            if (auth.getClass().isInstance(newAuthentication)) {
                entry.setValue(newAuthentication);
                return authentications;
            }
        }
        throw new RuntimeException("No " + newAuthentication.getClass() + " authentication configured!");
    }

}
