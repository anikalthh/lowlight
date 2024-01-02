package sg.nus.lowlight.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.PostComment;
import sg.nus.lowlight.repo.RedisRepository;

@Service
public class ChannelsService {
    
    @Autowired
    private RedisRepository repo;

    public void saveChannel(String channelName, String description) {
        if (Boolean.FALSE.equals(repo.channelExists(channelName))) {
            repo.saveChannel(channelName, description);
        } 
    }

    public List<Channel> getAllChannels() {
        return repo.getAllChannels();
    }

    public Post getPost(String channelName, String postId) {
        return repo.getPost(channelName, postId);
    }

    public void savePost(String channelName, Post post) {
        repo.savePost(channelName, post);
    }

    public void deletePost(String channelName, String postId) {
        repo.deletePost(channelName, postId);
    }

    public List<Post> getPostsFromChannel(String channelName) {
        return repo.getPostsFromChannel(channelName);
    }

    // COMMENTS REDIS METHODS: SAVE COMMENT
    public void saveComment(String postId, PostComment comment) {
        repo.saveComment(postId, comment);
    }

    // COMMENTS REDIS METHODS: DELETE COMMENT
    public void deleteComment(String postId, String commentId) {
        repo.deleteComment(postId, commentId);
    }

    // COMMENTS REDIS METHODS: GET COMMENTS
    public List<PostComment> getAllComments(String postId) {
        return repo.getAllComments(postId);
    }
}
