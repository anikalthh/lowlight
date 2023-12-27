package sg.nus.lowlight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping
public class RegistrationController {

    @Autowired
    private ChannelsService chSvc;

    @Autowired
    private UserService userSvc;
    
    @GetMapping("/login")
    public String getLoginPage(Model m) {
        // m.addAttribute("placeholderemail", "placeholder@gamil.com");
        m.addAttribute("userlogin", new User());
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage(Model m) {
        m.addAttribute("user", new User());
        return "signup";
    }

    // Validation for sign up
    @PostMapping("/signupsuccess")
    public String processSignup(@Valid @ModelAttribute User user, BindingResult bindings, Model m, HttpSession sess) throws JsonProcessingException {

        if (bindings.hasErrors()) {
            return "signup";
        }

        if (userSvc.userExists(user.getUsername())) {
            m.addAttribute("userexists", true);
            return "signup";
        }

        userSvc.saveUser(user);
        userSvc.registrationSaveUser(user.getUsername(), user.getPassword());
        List<Channel> listOfChannels = chSvc.getAllChannels();

        m.addAttribute("channels", listOfChannels);
        sess.setAttribute("currUser", user.getUsername());
        m.addAttribute("username", user.getUsername());

        // Update user's last active date
        userSvc.setLastActiveDate(user.getUsername());
        return "browsechannels";
    }

    // Validation for login
    @PostMapping("/loginsuccess")
    public String processLogin(@Valid @ModelAttribute("userlogin") User user, BindingResult bindings, Model m, HttpSession sess) throws JsonProcessingException {

        if (bindings.hasErrors()) {
            return "login";
        }

        if (!userSvc.loginAuthentication(user.getUsername(), user.getPassword())) {
            m.addAttribute("loginfailed", true);
            m.addAttribute("userlogin", new User());
            return "login";
        }
        
        List<Channel> listOfChannels = chSvc.getAllChannels();
        sess.setAttribute("currUser", user.getUsername());
        m.addAttribute("username", user.getUsername());
        m.addAttribute("channels", listOfChannels);

        // Update user's last active date
        userSvc.setLastActiveDate(user.getUsername());
        return "browsechannels";
    }

    // logout (invalidate session)
    @GetMapping("/logout")
    public String logout(HttpSession sess) {
        sess.invalidate();
        return "index";
    }

}
