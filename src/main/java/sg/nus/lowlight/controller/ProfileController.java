package sg.nus.lowlight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.service.ProfileService;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userSvc;

    @Autowired
    private ProfileService profSvc;
    
    @GetMapping("/{username}")
    public String getProfile(@PathVariable("username") String username, Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        String currUser = sess.getAttribute("currUser").toString();
        if (currUser.equals(username)) {
            m.addAttribute("isOwnProfile", true);
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
        }
        m.addAttribute("posts", profile.getPostsMade());

        return "profilepage";
    }
}
