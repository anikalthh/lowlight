package sg.nus.lowlight.controller;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import sg.nus.lowlight.service.ChatbotService;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotSvc;
    
    @GetMapping
    public String getChatbot(Model m, HttpSession sess) {

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            return "notloggedinerror";
        }

        // Link user query field
        m.addAttribute("userquery", "");

        // Create Linked Lists for user and chatbot messages
        sess.setAttribute("listOfUserMsgs", new LinkedList<>());
        sess.setAttribute("listOfChatbotMsgs", new LinkedList<>());

        // Add current user's username
        m.addAttribute("username", sess.getAttribute("currUser").toString());
        return "chatbot";
    }

    @PostMapping("/newquery")
    public String getChatbotResponse(@RequestBody MultiValueMap mvm, Model m, HttpSession sess) {

        // Get current user's username
        String username = sess.getAttribute("currUser").toString();

        // Send in user query to OpenAI API
        String userQuery = mvm.getFirst("userquery").toString();
        String response = chatbotSvc.getAIResponse(userQuery, username).replace("\n", "<br>");
        
        // Retrieve Linked Lists for past messages
        List<String> listOfUserMsgs = (List<String>) sess.getAttribute("listOfUserMsgs");
        List<String> listOfChatbotMsgs = (List<String>) sess.getAttribute("listOfChatbotMsgs");

        // Add user query and response to the list to display
        listOfUserMsgs.add(userQuery);
        listOfChatbotMsgs.add(response);

        // Update the Linked Lists in HTTPSession
        sess.setAttribute("listOfUserMsgs", listOfUserMsgs);
        sess.setAttribute("listOfChatbotMsgs", listOfChatbotMsgs);

        // Set Linked Lists as attributes for HTML Display
        m.addAttribute("listOfUserMsgs", listOfUserMsgs);
        m.addAttribute("listOfChatbotMsgs", listOfChatbotMsgs);

        // Set other model attributes
        m.addAttribute("userquery", ""); // Clear input field 
        m.addAttribute("userasked", true); // Show user and chatbot msg bubbles
        m.addAttribute("timenow", LocalDateTime.now());

        // Add current user's username
        m.addAttribute("username", username);
        return "chatbot";
    }
}
