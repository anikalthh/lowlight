package sg.nus.lowlight.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.utility.Utils;

@Service
public class LoadDummyDataService {
    
    public List<Channel> getDummyChannels() throws FileNotFoundException, IOException {
        File file = new File("src/main/resources/dummydata/channels.json");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while (null != (line=br.readLine())) {
                sb.append(line);
            }
        }

        String jsonStr = sb.toString();
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        JsonArray ja = jr.readArray();

        List<Channel> listOfChannels = ja.stream()
        .map(j -> j.asJsonObject())
        .map(o -> {
            String channelName = o.getString("channelName");
            String desciption = o.getString("description");
            return new Channel(channelName, desciption);
        })
        .toList();

        return listOfChannels;
    }

    public List<User> getDummyUsers() throws IOException {
        File file = new File("src/main/resources/dummydata/users.json");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while (null != (line=br.readLine())) {
                sb.append(line);
            }
        }

        String jsonStr = sb.toString();
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        JsonArray ja = jr.readArray();

        List<User> listOfUsers = ja.stream()
        .map(j -> j.asJsonObject())
        .map(o -> {
            return Utils.jsonStrToUserObj(o.toString());
        })
        .toList();

        return listOfUsers;
    }

    public List<Post> getDummyPosts() throws IOException {
        File file = new File("src/main/resources/dummydata/posts.json");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while (null != (line=br.readLine())) {
                sb.append(line);
            }
        }

        String jsonStr = sb.toString();
        JsonReader jr = Json.createReader(new StringReader(jsonStr));
        JsonArray ja = jr.readArray();

        List<Post> listOfPosts = ja.stream()
        .map(j -> j.asJsonObject())
        .map(o -> {
            String postId = o.getString("postId");
            String title = o.getString("title");
            String author = o.getString("author");
            String body = o.getString("body");
            long datePublished = o.getJsonNumber("datePublished").longValue();
            Boolean isAnonymous = o.getBoolean("isAnonymous");
            String mood = o.getString("mood");
            String channelName = o.getString("channelName");

            return new Post(postId, title, author, body, datePublished, isAnonymous, mood, channelName);
        })
        .toList();

        return listOfPosts;
    }
}
