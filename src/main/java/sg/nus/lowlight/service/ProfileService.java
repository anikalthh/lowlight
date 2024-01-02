package sg.nus.lowlight.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.utility.Utils;

@Service
public class ProfileService {

    @Autowired
    private UserService userSvc;

    private String quotesBaseUrl = "https://api.api-ninjas.com/v1/quotes";
    private String graphBaseUrl = "https://quickchart.io/chart";

    @Value("${quote.apikey}")
    private String apiKey;
    
    public JsonObject getRandomQuote() {

        String fullUrl = UriComponentsBuilder
            .fromUriString(quotesBaseUrl)
            .queryParam("category", "inspirational")
            .toUriString();

        RequestEntity<Void> req = RequestEntity
            .get(fullUrl)
            .header("X-Api-Key", apiKey)
            .build();
            
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> res = restTemplate.exchange(req, String.class);

        String resBody = res.getBody();

        JsonObject jsonObj = Json.createReader(new StringReader(resBody)).readArray().getJsonObject(0);

        return jsonObj;
    }

    // Limit the length of the random quote generated 
    public JsonObject getRandomQuoteAndCheckLimit() {
        JsonObject quoteObj = getRandomQuote();

        if(quoteObj.getString("quote").length() > 100) {
            return getRandomQuoteAndCheckLimit();
        }

        return quoteObj;
    }

    public String getGraphUrl(String username) {

        // Retrieve moodtracker data array
        User user = userSvc.getUserObj(username);
        Integer[] moodtrackerData = user.getMoodtracker();

        // Create Background colours JsonArray
        JsonArray jsonArrayBackgroundColor = Json.createArrayBuilder()
            .add("rgba(255, 205, 86, 0.5)")
            .add("rgba(255, 159, 64, 0.5)")
            .add("rgba(54, 162, 235, 0.5)")
            .add("rgba(255, 99, 132, 0.5)")
            .add("rgba(75, 192, 192, 0.5)")
            .build();

        // Create datasets JsonObject
        JsonObject jsonObjectDatasets = Json.createObjectBuilder()
            .add("data", Utils.convertMoodtrackerIntoJsonArray(moodtrackerData))
            .add("backgroundColor", jsonArrayBackgroundColor)
            .add("label", "Moodtracker dataset")
            .build();
        JsonArray jsonArrayDatasets = Json.createArrayBuilder()
            .add(jsonObjectDatasets)
            .build();

        // Create JsonArray Labels 
        JsonArray jsonArrayLabels = Json.createArrayBuilder()
            .add("Happy")
            .add("Peaceful")
            .add("Sad")
            .add("Angry")
            .add("Unmotivated")
            .build();

        // Create data JsonObject
        JsonObject jsonObjectData = Json.createObjectBuilder()
            .add("datasets", jsonArrayDatasets)
            .add("labels", jsonArrayLabels)
            .build();

        // Create options JsonObject
        JsonObject jsonObjectOptions = Json.createObjectBuilder()
            .add("legend", Json.createObjectBuilder().add("position", "right"))
            .add("title", Json.createObjectBuilder().add("display", true).add("text", "My Moodtracker"))
            .build();

        // Create the full JsonObject
        JsonObject jsonObjFull = Json.createObjectBuilder()
            .add("type", "polarArea")
            .add("data", jsonObjectData)
            .add("options", jsonObjectOptions)
            .build();

        String fullUrl = UriComponentsBuilder
            .fromUriString(graphBaseUrl)
            .queryParam("c", jsonObjFull)
            .toUriString();

        return fullUrl;
    }
}
