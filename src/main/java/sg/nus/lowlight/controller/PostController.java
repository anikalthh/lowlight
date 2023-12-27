package sg.nus.lowlight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping("/channels/{channelName}")
public class PostController {

    @Autowired
    private ChannelsService chSvc;

    @Autowired
    private UserService userSvc;

    @GetMapping("/newpost")
    public String createNewPost(@PathVariable("channelName") String channelName, Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }
        
        Post newPost = new Post();
        newPost.setAuthor(sess.getAttribute("currUser").toString());
        m.addAttribute("newpost", newPost);
        m.addAttribute("channelName", channelName);
        sess.setAttribute("channelName", channelName);
        return "addnewpost";
    }

    @PostMapping("/success")
    public String postAdded(@Valid @ModelAttribute("newpost") Post newpost, BindingResult bindings, Model m, HttpSession sess) throws JsonProcessingException {
        if (bindings.hasErrors()) {
            return "addnewpost";
        }

        String channelName = sess.getAttribute("channelName").toString();
        String username = sess.getAttribute("currUser").toString();
        chSvc.savePost(channelName, newpost);
        m.addAttribute("isnewpostadded", true);
        m.addAttribute("channelName", channelName);

        List<Post> listOfPosts = chSvc.getPostsFromChannel(channelName);
        m.addAttribute("posts", listOfPosts);
        m.addAttribute("username", username);

        // Update user's published posts
        userSvc.addPostToUserDetails(username, newpost);

        // Update user's moodtracker
        userSvc.addMoodToUserDetails(username, newpost.getMood());
        
        m.addAttribute("username", username);
        return "browseposts";
    }
    
}
