package sg.nus.lowlight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.ProfileService;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userSvc;

    @Autowired
    private ProfileService profSvc;

    @Autowired
    private ChannelsService chSvc;
    
    @GetMapping("/{username}")
    public String getProfile(@PathVariable("username") String username, Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        // Populate profile page
        String currUser = sess.getAttribute("currUser").toString();
        if (currUser.equals(username)) {
            m.addAttribute("isOwnProfile", true);
        } else {
            m.addAttribute("isOwnProfile", false);
        }
        
        // Retrieve userdata
        User profile = userSvc.getUserObj(username);

        // User data
        m.addAttribute("email", profile.getEmail());
        m.addAttribute("lastviewedchannel", profile.getLastViewedChannel());
        m.addAttribute("lastactivedate", profile.getLastActiveDate());
        if (profile.getLastViewedChannel().equals("User has not viewed any channels yet.")) {
            m.addAttribute("hasNotViewedChannels", true);
        }

        // Retrieve Quotes API
        JsonObject jsonObj = profSvc.getRandomQuoteAndCheckLimit();
        m.addAttribute("quote", jsonObj.getString("quote"));
        m.addAttribute("quoteauthor", jsonObj.getString("author"));

        // Retrieve Graph API
        String graphUrl = profSvc.getGraphUrl(username);
        m.addAttribute("graphAPI", graphUrl);

        // Retrieve user's posts made
        if (profile.getPostsMade().size() == 0) {
            m.addAttribute("hasnoposts", true);
        } else {
            m.addAttribute("hasnoposts", false);
        }
        m.addAttribute("posts", profile.getPostsMade());

        return "profilepage";
    }

    @PostMapping("{username}/deletepost")
    public String deleteEntry(@PathVariable("username") String username, @RequestBody MultiValueMap mvm, HttpSession sess, Model m) throws JsonProcessingException {
        
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        // Delete post logic: remove from user details
        Integer entryIndex = Integer.parseInt(mvm.getFirst("index").toString());
        userSvc.deletePostFromUserDetails(username, entryIndex);

        // Delete post logic: remove from channels 
        String postId = mvm.getFirst("postId").toString();
        String channelName = mvm.getFirst("channelName").toString();
        chSvc.deletePost(channelName, postId);

        // Populate profile page
        String currUser = sess.getAttribute("currUser").toString();
        if (currUser.equals(username)) {
            m.addAttribute("isOwnProfile", true);
        } else {
            m.addAttribute("isOwnProfile", false);
        }
        
        // Retrieve userdata
        User profile = userSvc.getUserObj(username);

        // User data
        m.addAttribute("email", profile.getEmail());
        m.addAttribute("lastviewedchannel", profile.getLastViewedChannel());
        m.addAttribute("lastactivedate", profile.getLastActiveDate());
        if (profile.getLastViewedChannel().equals("User has not viewed any channels yet.")) {
            m.addAttribute("hasNotViewedChannels", true);
        }

        // Retrieve Quotes API
        JsonObject jsonObj = profSvc.getRandomQuoteAndCheckLimit();
        m.addAttribute("quote", jsonObj.getString("quote"));
        m.addAttribute("quoteauthor", jsonObj.getString("author"));

        // Retrieve Graph API
        String graphUrl = profSvc.getGraphUrl(username);
        m.addAttribute("graphAPI", graphUrl);

        // Retrieve user's posts made
        if (profile.getPostsMade().size() == 0) {
            m.addAttribute("hasnoposts", true);
        } else {
            m.addAttribute("hasnoposts", false);
        }
        m.addAttribute("posts", profile.getPostsMade());
        m.addAttribute("deletedpost", true);
        return "profilepage";
    }
}
