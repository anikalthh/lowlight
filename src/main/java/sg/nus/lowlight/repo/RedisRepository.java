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
import sg.nus.lowlight.model.PostComment;
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

    public Post getPost(String channelName, String postId) {
        String postJsonStr = template.opsForHash().get(channelName, postId).toString();
        Post post = Utils.jsonStrToPostObj(postJsonStr);
        return post;
    }

    public void savePost(String channelName, Post post) {
        JsonObject jsonObj = Utils.postObjTojsonObj(post);
        String jsonStr = jsonObj.toString();
        template.opsForHash().put(channelName, post.getPostId(), jsonStr);
    }

    public void deletePost(String channelName, String postId) {
        template.opsForHash().delete(channelName, postId);
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

    // COMMENTS REDIS METHODS: SAVE COMMENT
    public void saveComment(String postId, PostComment comment) {
        StringBuilder sb = new StringBuilder();
        String commentsPostIdRef = sb
            .append("comments-")
            .append(postId)
            .toString();

        String commentJsonStr = Utils.commentObjToJsonObj(comment).toString();
        template.opsForHash().put(commentsPostIdRef, comment.getCommentId(), commentJsonStr);
    }

    // COMMENTS REDIS METHODS: DELETE COMMENT
    public void deleteComment(String postId, String commentId) {
        StringBuilder sb = new StringBuilder();
        String commentsPostIdRef = sb
            .append("comments-")
            .append(postId)
            .toString();

        template.opsForHash().delete(commentsPostIdRef, commentId);
    }

    // COMMENTS REDIS METHODS: GET COMMENTS
    public List<PostComment> getAllComments(String postId) {
        List<PostComment> listOfComments = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        String commentsPostIdRef = sb
            .append("comments-")
            .append(postId)
            .toString();

        List<Object> comments = template.opsForHash().values(commentsPostIdRef);

        for (Object o : comments) {
            PostComment comment = Utils.commentStrToCommentObj(o.toString());
            listOfComments.add(comment);
        }

        return listOfComments;
    }
}
