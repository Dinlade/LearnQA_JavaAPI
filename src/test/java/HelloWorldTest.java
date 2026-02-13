import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HelloWorldTest {



    @Test
    public void testLongTimeJob() throws InterruptedException {


        JsonPath createJobResponse = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = createJobResponse.getString("token");
        int seconds = createJobResponse.getInt("seconds");

        System.out.println("Token: " + token);
        System.out.println("Seconds to wait: " + seconds);

        JsonPath notReadyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusBefore = notReadyResponse.getString("status");

        if (!"Job is NOT ready".equals(statusBefore)) {
            throw new RuntimeException(
                    "Unexpected status before job is ready: " + statusBefore
            );
        }

        if (notReadyResponse.get("result") != null) {
            throw new RuntimeException(
                    "Result should be absent before job is ready"
            );
        }

        Thread.sleep(seconds * 1000L);

        JsonPath readyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusAfter = readyResponse.getString("status");

        if (!"Job is ready".equals(statusAfter)) {
            throw new RuntimeException(
                    "Unexpected status after job is ready: " + statusAfter
            );
        }

        if (readyResponse.get("result") == null) {
            throw new RuntimeException(
                    "Result should be present after job is ready"
            );
        }

        System.out.println("Job completed successfully");
        System.out.println("Result: " + readyResponse.getString("result"));
    }

    @Test
    public void testRestAssured(){
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");
        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .get("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookies = response.getCookie("auth_cookie");
        System.out.println(responseCookies);
    }

    @Test
    public void testGetSecondMessage() {

        JsonPath response = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = response.getString("messages[1].message");

        System.out.println("Second message text: " + secondMessage);
    }

    @Test
    public void testRedirect(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("location");
        System.out.println(locationHeader);
    }


    @Test
    public void testLongRedirect() {

        String url = "https://playground.learnqa.ru/api/long_redirect";
        int redirectCount = 0;
        int statusCode;

        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url);

            statusCode = response.getStatusCode();

            if (statusCode != 200) {
                url = response.getHeader("Location");
                redirectCount++;
                System.out.println("Redirect to: " + url);
            }

        } while (statusCode != 200);

        System.out.println("Final URL: " + url);
        System.out.println("Redirect count: " + redirectCount);
    }


    @Test
    public void testFindPassword() {

        String login = "super_admin";

        String[] passwords = {
                "123456", "password", "12345678", "qwerty", "123456789",
                "12345", "1234", "111111", "1234567", "dragon",
                "123123", "baseball", "abc123", "football", "monkey",
                "letmein", "shadow", "master", "666666", "qwertyuiop",
                "123321", "mustang", "1234567890", "michael", "654321"
        };

        for (String password : passwords) {

            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", password);

            Response loginResponse = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String authCookie = loginResponse.getCookie("auth_cookie");

            Response checkResponse = RestAssured
                    .given()
                    .cookie("auth_cookie", authCookie)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String responseText = checkResponse.getBody().asString();

            if (!responseText.equals("You are NOT authorized")) {
                System.out.println("PASSWORD FOUND!");
                System.out.println("Password: " + password);
                System.out.println("Response: " + responseText);
                assertEquals("You are authorized", responseText);
                break;
            }
        }
    }

}


