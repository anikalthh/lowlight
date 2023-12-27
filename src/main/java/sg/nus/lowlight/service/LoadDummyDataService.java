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
}
