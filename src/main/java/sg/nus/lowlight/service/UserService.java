package sg.nus.lowlight.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import sg.nus.lowlight.model.JournalEntry;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    // Check if user exists
    public Boolean userExists(String username) {
        return userRepo.userExists(username);
    }

    // Save User
    public void saveUser(User user) throws JsonProcessingException {
        if (!userRepo.userExists(user.getUsername())) {
            userRepo.saveUser(user);
        }
    }

    // Save username and password upon registration
    public void registrationSaveUser(String username, String password) {
        if (!userRepo.userExists(username)) {
            userRepo.saveUsernameAndPassword(username, password);
        }
    }

    // Login authentication: Check username and password
    public Boolean loginAuthentication(String username, String password) {
        if (!userRepo.userExists(username)) {
            return false;
        } else {
            String correctPassword = userRepo.getPassword(username);
            if (correctPassword.equals(password)) {
                return true;
            }
            return false;
        }
    }

    // Retrieve User Object
    public User getUserObj(String username) {
        return userRepo.getUserObj(username);
    }

    public String getUserObjInString(String username) {
        return userRepo.getUserObjInString(username);
    }

    // Get all journal entries of a user
    public List<JournalEntry> getAllEntries(String username) {
        User user = userRepo.getUserObj(username);
        return user.getEntriesMade();
    }

    // UPDATE USER DETAILS
    // Last Viewed Channel
    public void setLastViewedChannel(String username, String channelName) throws JsonProcessingException {
        userRepo.setLastViewedChannel(username, channelName);
    }

    // Last Active Date
    public void setLastActiveDate(String username) throws JsonProcessingException {
        userRepo.setActiveDateAsNow(username);
    }

    // Add Post to User Details
    public void addPostToUserDetails(String username, Post post) throws JsonProcessingException {
        userRepo.addPostToUserDetails(username, post);
    }

    // Add Mood to User Details
    public void addMoodToUserDetails(String username, String mood) throws JsonProcessingException {
        userRepo.addMood(username, mood);
    }

    // Add Journal Entry to User Details
    public void addJournalEntryToUserDetails(String username, JournalEntry entry) throws JsonProcessingException {
        userRepo.addJournalEntryToUserDetails(username, entry);
    }
}
