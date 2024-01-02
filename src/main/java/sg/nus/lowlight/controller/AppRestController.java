package sg.nus.lowlight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import sg.nus.lowlight.service.UserService;

@RestController
@RequestMapping(path = "/restapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppRestController {

    @Autowired
    private UserService userSvc;

    @GetMapping
    public ModelAndView getRestAPI(HttpSession sess) {
        ModelAndView mav = new ModelAndView();

        // If user is not logged in, redirect to error page
        if (sess.getAttribute("currUser") == null) {
            mav.setViewName("notloggedinerror");
            return mav;
        }

        mav.setViewName("restapi");
        mav.addObject("username", sess.getAttribute("currUser"));
        return mav;
    }

    
    @GetMapping(path = "/info")
    public ResponseEntity<String> getUserDetailsJson(@RequestParam("username") String username) {

        // Retrieve User details
        String jsonObj = userSvc.getUserObjInString(username);

        return new ResponseEntity<>(jsonObj, HttpStatusCode.valueOf(200));
    }

    @GetMapping(path = "/info/allusers")
    public ResponseEntity<String> getAllUserDetailsJson() {

        // Retrieve User details
        String jsonObj = userSvc.getAllUserObjsInString();

        return new ResponseEntity<>(jsonObj, HttpStatusCode.valueOf(200));
    }
}
