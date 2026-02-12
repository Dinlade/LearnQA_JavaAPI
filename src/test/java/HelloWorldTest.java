import io.restassured.RestAssured;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class HelloWorldTest {

    @Test
    public void testRestAssured(){
        Map<String, String> headers = new HashMap<>();
        headers.put("MyHeaders1", "MyValue1");
        headers.put("MyHeaders2", "MyValue2");
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("location");
        System.out.println(locationHeader);
    }

    @Test
    public void testJson(){
        Map<String, String> params = new HashMap<>();
        params.put("2021-06-04 16:41:51", "And this is a second message");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

//        response.prettyPrint();
        String answer = response.get("answer");
        System.out.println(answer);
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
    public void testText(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }




}
