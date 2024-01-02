package sg.nus.lowlight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.nus.lowlight.model.JournalEntry;
import sg.nus.lowlight.service.UserService;
import sg.nus.lowlight.utility.Utils;

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

        // If currUser tries to view other users' journals, redirect them to error page
        String currUser = sess.getAttribute("currUser").toString();
        if (!currUser.equals(username)) {
            m.addAttribute("username", currUser);
            return "journalerror";
        }
        List<JournalEntry> listOfEntries = userSvc.getAllEntries(username);
        List<JournalEntry> sortedEntries = Utils.sortByDateEntries(listOfEntries);
        if (listOfEntries.size() == 0) {
            m.addAttribute("noentriesyet", true);
        }

        // Retrieve all journal entries made by the user
        m.addAttribute("journalentries", sortedEntries);
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
        List<JournalEntry> listOfEntries = userSvc.getAllEntries(username);
        List<JournalEntry> sortedEntries = Utils.sortByDateEntries(listOfEntries);

        m.addAttribute("journalentries", sortedEntries);
        m.addAttribute("username", username);
        m.addAttribute("newentryadded", true);
        return "journal";
    }

    @PostMapping("/delete")
    public String deleteEntry(@RequestBody MultiValueMap mvm, HttpSession sess, Model m) throws JsonProcessingException {
        String username = sess.getAttribute("currUser").toString();
        Integer entryIndex = Integer.parseInt(mvm.getFirst("index").toString());
        userSvc.deleteJournalEntryFromUserDetails(username, entryIndex);
        
        // Retrieve all journal entries made by the user
        List<JournalEntry> listOfEntries = userSvc.getAllEntries(username);
        List<JournalEntry> sortedEntries = Utils.sortByDateEntries(listOfEntries);

        m.addAttribute("journalentries", sortedEntries);
        m.addAttribute("username", username);
        m.addAttribute("deletedpost", true);
        return "journal";
    }
}
