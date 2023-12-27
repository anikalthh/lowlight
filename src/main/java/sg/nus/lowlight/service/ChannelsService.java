package sg.nus.lowlight.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.repo.RedisRepository;

@Service
public class ChannelsService {
    
    @Autowired
    private RedisRepository repo;

    public void saveChannel(String channelName, String description) {
        if (!repo.channelExists(channelName)) {
            repo.saveChannel(channelName, description);
        } 
    }

    public List<Channel> getAllChannels() {
        return repo.getAllChannels();
    }

    public void savePost(String channelName, Post post) {
        repo.savePost(channelName, post);
    }

    public List<Post> getPostsFromChannel(String channelName) {
        return repo.getPostsFromChannel(channelName);
    }
}
