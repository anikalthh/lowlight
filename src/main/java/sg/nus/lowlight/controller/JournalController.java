package sg.nus.lowlight.controller;

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
import sg.nus.lowlight.model.JournalEntry;
import sg.nus.lowlight.service.UserService;

@Controller
@RequestMapping("/profile/{username}/journal")
public class JournalController {

    @Autowired
    private UserService userSvc;
    
    @GetMapping
    public String getJournalEntries(@PathVariable("username") String username, Model m, HttpSession sess) {
        
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }
        
        // Retrieve all journal entries made by the user
        m.addAttribute("journalentries", userSvc.getAllEntries(username));
        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "journal";
    }

    @GetMapping("/newentry") 
    public String createNewEntry(Model m, HttpSession sess) {
        
        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }
        // Save journal entry into user object in redis
        m.addAttribute("newjournalentry", new JournalEntry());
        m.addAttribute("username", sess.getAttribute("currUser"));
        return "addnewentry";
    }

    @PostMapping("/success")
    public String processEntry(@Valid @ModelAttribute("newjournalentry") JournalEntry newentry, BindingResult bindings, Model m, HttpSession sess) throws JsonProcessingException {

        // Retrieve current user's username
        String username = sess.getAttribute("currUser").toString();

        // Save Journal Entry and add to user object
        userSvc.addJournalEntryToUserDetails(username, newentry);

        // Update user's moodtracker
        userSvc.addMoodToUserDetails(username, newentry.getMood());

        // Retrieve all journal entries made by the user
        m.addAttribute("journalentries", userSvc.getAllEntries(username));
        m.addAttribute("username", username);
        return "journal";
    }
}
