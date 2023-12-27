package sg.nus.lowlight.utility;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.nus.lowlight.model.JournalEntry;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.User;

public class Utils {

    // POST OBJECT UTILITY
    public static JsonObject postObjTojsonObj(Post post) {
        JsonObject jsonObj = Json.createObjectBuilder()
            .add("title", post.getTitle())
            .add("author", post.getAuthor())
            .add("body", post.getBody())
            .add("datePublished", post.getDatePublished())
            .add("isAnonymous", post.getIsAnonymous())
            .add("mood", post.getMood())
            .build();

        return jsonObj;
    }

    public static Post jsonStrToPostObj(String jsonStr) {
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        JsonObject jsonObj = jr.readObject();

        String title = jsonObj.getString("title");
        String author = jsonObj.getString("author");
        String body = jsonObj.getString("body");
        long datePublished = jsonObj.getJsonNumber("datePublished").longValue();
        Boolean isAnonymous = jsonObj.getBoolean("isAnonymous");
        String mood = jsonObj.getString("mood");

        return new Post(title, author, body, datePublished, isAnonymous, mood);
    }

    // JOURNAL ENTRY OBJECT UTILITY
    public static JsonObject journalEntryObjTojsonObj(JournalEntry entry) {
        JsonObject jsonObj = Json.createObjectBuilder()
            .add("title", entry.getTitle())
            .add("body", entry.getBody())
            .add("datePublished", entry.getDatePublished())
            .add("mood", entry.getMood())
            .build();

        return jsonObj;
    }

    // USER OBJECT UTILITY
    public static JsonObject userObjTojsonObj(User user) throws JsonProcessingException {
        System.out.printf("USER OBJECT UTILTIY, IS ENTRIES EMPTY: %s\n", user.getEntriesMade());
        JsonObject jsonObj = Json.createObjectBuilder()
            .add("username", user.getUsername())
            .add("email", user.getEmail())
            .add("lastViewedChannel", user.getLastViewedChannel())
            .add("lastActiveDate", user.getLastActiveDate())
            .add("moodtracker", convertMoodtrackerIntoJsonArray(user.getMoodtracker()))
            .add("postsMade", convertListOfPostsIntoJsonArray(user.getPostsMade()))
            .add("entriesMade", convertListOfEntriesIntoJsonArray(user.getEntriesMade()))
            .build();

        return jsonObj;
    }

    public static User jsonStrToUserObj(String jsonStr) {
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        JsonObject jsonObj = jr.readObject();

        String username = jsonObj.getString("username");
        String email = jsonObj.getString("email");
        String lastViewedChannel = jsonObj.getString("lastViewedChannel");
        long lastActiveDate = jsonObj.getJsonNumber("lastActiveDate").longValue();

        Integer[] moodtracker = jsonObj.getJsonArray("moodtracker")
            .stream()
            .map(j -> ((JsonNumber) j).intValue())
            .toArray(Integer[]::new);
        
        // List<Post>
        JsonArray postsMadeJsonArray = jsonObj.getJsonArray("postsMade");

        List<Post> postsMade = postsMadeJsonArray.stream()
            .map((j -> j.asJsonObject()))
            .map(o -> {
                String title = o.getString("title");
                String author = o.getString("author");
                String body = o.getString("body");
                long datePublished = o.getJsonNumber("datePublished").longValue();
                Boolean isAnonymous = o.getBoolean("isAnonymous");
                String mood = o.getString("mood");

                return new Post(title, author, body, datePublished, isAnonymous, mood);
            })
            .collect(Collectors.toList());

        // List<JournalEntry>
        JsonArray entriesMadeJsonArray = jsonObj.getJsonArray("entriesMade");

        List<JournalEntry> entriesMade = entriesMadeJsonArray.stream()
            .map((j -> j.asJsonObject()))
            .map(o -> {
                String title = o.getString("title");
                String body = o.getString("body");
                long datePublished = o.getJsonNumber("datePublished").longValue();
                String mood = o.getString("mood");

                return new JournalEntry(title, body, datePublished, mood);
            })
            .collect(Collectors.toList());

        return new User(username, "hidden", email, lastViewedChannel, lastActiveDate, moodtracker, postsMade, entriesMade);
    }

    public static JsonObject jsonStrToUserJsonObj(String jsonStr) {
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        return jr.readObject();
    }

    // SUBMETHODS: CONVERT List<Post> into JsonArray
    public static JsonArray convertListOfPostsIntoJsonArray(List<Post> listOfPosts) throws JsonProcessingException {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (Post post : listOfPosts) {
            JsonObject postJsonObj = postObjTojsonObj(post);
            jsonArrayBuilder.add(postJsonObj);
        }

        JsonArray jsonArray = jsonArrayBuilder.build();
        return jsonArray;
    }

    // SUBMETHODS: CONVERT List<Entries> into JsonArray
    public static JsonArray convertListOfEntriesIntoJsonArray(List<JournalEntry> listOfEntries) throws JsonProcessingException {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (JournalEntry entry : listOfEntries) {
            JsonObject entryJsonObj = journalEntryObjTojsonObj(entry);
            jsonArrayBuilder.add(entryJsonObj);
        }

        JsonArray jsonArray = jsonArrayBuilder.build();
        System.out.printf("SAVING INTO JSONARRAY: %s\n", jsonArray);
        return jsonArray;
    }

    // SUBMETHODS: CONVERT Integer[] into JsonArray
    public static JsonArray convertMoodtrackerIntoJsonArray(Integer[] moodtracker) {
        JsonArray jsonArray = Json.createArrayBuilder()
            .add(moodtracker[0])
            .add(moodtracker[1])
            .add(moodtracker[2])
            .add(moodtracker[3])
            .add(moodtracker[4])
            .build();

        return jsonArray;
    }
}
