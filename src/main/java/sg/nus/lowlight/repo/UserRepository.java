package sg.nus.lowlight.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import sg.nus.lowlight.model.JournalEntry;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.utility.Utils;

@Repository
public class UserRepository {

    @Autowired @Qualifier("redis") 
    private RedisTemplate<String, String> template;

    private String passwordsHashRef = "passwords";
    private String userDetailsHashRef = "users";

    // Check if username exists in password hashmap
    public Boolean userExists(String username) {
        return template.opsForHash().hasKey("passwords", username);
    }

    // Retrieve password using username
    public String getPassword(String username) {
        return template.opsForHash().get("passwords", username).toString();
    }

    // Save User object in Redis
    public void saveUser(User user) throws JsonProcessingException {
        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, user.getUsername(), userJsonStr);
    }

    // Save username and password
    public void saveUsernameAndPassword(String username, String password) {
        template.opsForHash().put(passwordsHashRef, username, password);
    }

    // Retrieve user object 
    public User getUserObj(String username) {
        String userJsonStr = template.opsForHash().get("users", username).toString();
        User userObj = Utils.jsonStrToUserObj(userJsonStr);
        return userObj;
    } 

    public String getUserObjInString(String username) {
        return template.opsForHash().get("users", username).toString();
    }

    // UPDATE USER DETAILS

    // Add new post to list of posts made
    public void addPostToUserDetails(String username, Post post) throws JsonProcessingException {
        String jsonStr = template.opsForHash().get(userDetailsHashRef, username).toString();
        User user = Utils.jsonStrToUserObj(jsonStr);
        user.addPost(post);
        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, username, userJsonStr);
    }

    // Add new journal entry to list of entries made
    public void addJournalEntryToUserDetails(String username, JournalEntry entry) throws JsonProcessingException {
        System.out.printf("ADDING ENTRY IN USER REPO JAVA CLASS: %s\n", entry);
        String jsonStr = template.opsForHash().get(userDetailsHashRef, username).toString();
        User user = Utils.jsonStrToUserObj(jsonStr);
        user.addJournalEntry(entry);
        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, username, userJsonStr);
    }

    // Moodtracker
    public void addMood(String username, String mood) throws JsonProcessingException {
        String jsonStr = template.opsForHash().get(userDetailsHashRef, username).toString();
        User user = Utils.jsonStrToUserObj(jsonStr);
        user.addMood(mood);

        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, username, userJsonStr);
    }

    // Last active date
    public void setActiveDateAsNow(String username) throws JsonProcessingException {
        String jsonStr = template.opsForHash().get(userDetailsHashRef, username).toString();
        User user = Utils.jsonStrToUserObj(jsonStr);
        user.setLastActiveDate(System.currentTimeMillis());

        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, username, userJsonStr);
    }

    // Last Viewed Channel
    public void setLastViewedChannel(String username, String channelName) throws JsonProcessingException {
        String jsonStr = template.opsForHash().get(userDetailsHashRef, username).toString();
        User user = Utils.jsonStrToUserObj(jsonStr);
        user.setLastViewedChannel(channelName);

        String userJsonStr = Utils.userObjTojsonObj(user).toString();
        template.opsForHash().put(userDetailsHashRef, username, userJsonStr);
    }
}
