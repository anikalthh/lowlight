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
import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    private ChannelsService chSvc;

    @Autowired
    private UserService userSvc;

    @GetMapping
    public String browseChannels(Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        List<Channel> listOfChannels = chSvc.getAllChannels();

        m.addAttribute("channels", listOfChannels);
        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "browsechannels";
    }

    @PostMapping("/success")
    public String channelAdded(@Valid @ModelAttribute("newchannel") Channel newchannel, BindingResult bindings, Model m, HttpSession sess) {
        if (bindings.hasErrors()) {
            return "addnewchannel";
        }

        chSvc.saveChannel(newchannel.getChannelName(), newchannel.getDescription());
        m.addAttribute("isnewchanneladded", true);
        m.addAttribute("username", sess.getAttribute("username").toString());
        return "browsechannels";
    }

    @GetMapping("/{channelName}") 
    public String getChannelPage(@PathVariable("channelName") String channelName, Model m, HttpSession sess) throws JsonProcessingException {
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        List<Post> listOfPosts = chSvc.getPostsFromChannel(channelName);

        m.addAttribute("channelName", channelName);
        m.addAttribute("posts", listOfPosts);

        // Last Viewed Channel
        String username = sess.getAttribute("currUser").toString();
        userSvc.setLastViewedChannel(username, channelName);

        m.addAttribute("username", username);
        return "browseposts";
    }
    
    @GetMapping("/newchannel")
    public String createNewChannel(Model m, HttpSession sess) {
        
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        m.addAttribute("newchannel", new Channel());
        return "addnewchannel";
    }
}
