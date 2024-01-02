package sg.nus.lowlight.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class ChatbotService {

    private String openAIbaseUrl = "https://api.openai.com/v1/chat/completions";
    // private String apiKey = "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";
    
    @Value("${openai.apikey}")
    private String apiKey;

    public String getAIResponse(String message, String username) {
        String[] messagesArr = {message};
        String messagesArrStr = createMessagesArray(messagesArr, username);
        String fullUrl = UriComponentsBuilder.fromUriString(openAIbaseUrl).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Set up your request payload manually
        String requestPayload = "{ \"model\": \"gpt-3.5-turbo\", \"messages\": " + messagesArrStr + ", \"max_tokens\": 300 }";

        // Create an HttpEntity with the request payload and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);

        RestTemplate restTemplate = new RestTemplate();

        // Use exchange for POST request
        ResponseEntity<String> res = restTemplate.exchange(fullUrl, HttpMethod.POST, requestEntity, String.class);
        System.out.printf("print res: %s\n", res);
        String response = getResponse(res.getBody());

        return response;
    }

    private String getResponse(String body) {
        JsonObject choicesJsonObj = Json.createReader(new StringReader(body)).readObject();
        JsonObject choices = choicesJsonObj.getJsonArray("choices").getJsonObject(0);
        JsonObject messageJsonObj = choices.getJsonObject("message");
        String content = messageJsonObj.getString("content");
        return content;
    }

    private String createMessagesArray(String[] messages, String username) {
        StringBuilder messagesArray = new StringBuilder("[");
        for (String message : messages) {

            // System message: No crafted error message
            messagesArray.append("{ \"role\": \"system\", \"content\": \"You are a chatbot in a mental health app. If the user asks anything unrelated to mental health or wellbeing, please respond appropriately.\"},");

            // User question
            messagesArray.append("{ \"role\": \"user\", \"content\": \" If I ask an irrelevant question, please respond appropriately. Question from " + username + ":" + message + "\" },");
        }

        // Remove the trailing comma for the last message
        messagesArray.setLength(messagesArray.length() - 1);
        messagesArray.append("]");

        return messagesArray.toString();
    }
}
