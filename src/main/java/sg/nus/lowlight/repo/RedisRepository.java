package sg.nus.lowlight.repo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonObject;
import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.utility.Utils;

@Repository
public class RedisRepository {

    @Autowired @Qualifier("redis") 
    private RedisTemplate<String, String> template;

    private String channelsHashRef = "allChannels";

    public void saveChannel(String channelName, String description) {
        template.opsForHash().put(channelsHashRef, channelName, description);
    }

    public Boolean channelExists(String channelName) {
        return template.hasKey(channelName); // e.g university_channel
    }

    public List<Channel> getAllChannels() {
        List<Channel> listOfChannels = new LinkedList<>();
        Map<Object, Object> channels = template.opsForHash().entries(channelsHashRef);
        
        for (Object key : channels.keySet()) {
            String channelName = key.toString();
            String desc = channels.get(channelName).toString();
            listOfChannels.add(new Channel(channelName, desc));
        }

        return listOfChannels;
    }

    public void savePost(String channelName, Post post) {
        JsonObject jsonObj = Utils.postObjTojsonObj(post);
        String jsonStr = jsonObj.toString();
        template.opsForHash().put(channelName, post.getTitle(), jsonStr);
    }

    public List<Post> getPostsFromChannel(String channelName) {
        List<Post> listOfPosts = new LinkedList<>();
        Map<Object, Object> posts = template.opsForHash().entries(channelName);

        for (Object post : posts.values()) {
            String postStr = post.toString();
            Post postObj = Utils.jsonStrToPostObj(postStr);
            listOfPosts.add(postObj);
        }

        return listOfPosts;
    }
}
