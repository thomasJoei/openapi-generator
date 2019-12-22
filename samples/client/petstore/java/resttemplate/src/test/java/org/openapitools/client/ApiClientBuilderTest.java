package org.openapitools.client;

import org.junit.Test;
import org.openapitools.client.auth.Authentication;
import org.openapitools.client.auth.HttpBasicAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ApiClientBuilderTest {


  @Test
  public void testBasePath() {
    ApiClient apiClient = new ApiClientBuilder()
        .basePath("base-path")
        .build();

    assertEquals("base-path", apiClient.getBasePath());
  }


  @Test
  public void testBasicAuth() {
    HttpBasicAuth httpBasicAuth = new HttpBasicAuth();
    httpBasicAuth.setUsername("my-username");
    httpBasicAuth.setPassword("my-password");

    Map<String, Authentication> authentications = new HashMap<>();
    authentications.put("http_basic_test", httpBasicAuth);

    ApiClient apiClient = new ApiClientBuilder()
        .authentications(authentications)
        .build();


    Map<String, Authentication> auths = apiClient.getAuthentications();
    HttpBasicAuth basicAuth = (HttpBasicAuth) auths.get("http_basic_test");
    assertEquals("my-username", basicAuth.getUsername());
    assertEquals("my-password", basicAuth.getPassword());

    try {
      auths.put("my_auth", new HttpBasicAuth());
      fail("the authentications returned should not be modifiable");
    } catch (UnsupportedOperationException e) {
    }
  }

  @Test
  public void defaultDateFormat() {
    ApiClient apiClient = new ApiClientBuilder()
        .build();

    String dateStr = "2015-11-07T03:49:09.356Z";
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T03:49:09.356+00:00")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T03:49:09.356Z")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T05:49:09.356+02:00")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T02:49:09.356-01:00")));

  }

  @Test
  public void dateFormat() {
    // custom date format: without milli-seconds, custom time zone
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT);
    format.setTimeZone(TimeZone.getTimeZone("GMT+10"));

    ApiClient apiClient = new ApiClientBuilder()
        .dateFormat(format)
        .build();

    String dateStr = "2015-11-07T13:49:09+10:00";
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T03:49:09+00:00")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T03:49:09Z")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T00:49:09-03:00")));
    assertEquals(dateStr, apiClient.formatDate(apiClient.parseDate("2015-11-07T13:49:09+10:00")));
  }

  @Test
  public void modifyDateFormat() {
    // custom date format: without milli-seconds, custom time zone
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT);
    format.setTimeZone(TimeZone.getTimeZone("GMT+10"));

    ApiClient apiClient = new ApiClientBuilder()
        .dateFormat(format)
        .build();

    apiClient.getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT+11"));
  }

  @Test
  public void basicAuth() {
    HttpBasicAuth httpBasicAuth = new HttpBasicAuth();
    httpBasicAuth.setUsername("my-username");
    httpBasicAuth.setPassword("my-password");

    ApiClient apiClient = new ApiClientBuilder()
        .basicAuth(httpBasicAuth)
        .build();


    Map<String, Authentication> auths = apiClient.getAuthentications();
    HttpBasicAuth basicAuth = (HttpBasicAuth) auths.get("http_basic_test");
    assertEquals("my-username", basicAuth.getUsername());
    assertEquals("my-password", basicAuth.getPassword());

    try {
      auths.put("my_auth", new HttpBasicAuth());
      fail("the authentications returned should not be modifiable");
    } catch (UnsupportedOperationException e) {
    }
  }



}
