package sg.nus.lowlight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.service.UserService;
import sg.nus.lowlight.utility.Utils;

@RestController
@RequestMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppRestController {

    @Autowired
    private UserService userSvc;
    
    @GetMapping(path = "/{username}")
    public ResponseEntity<String> getUserDetailsJson(@PathVariable("username") String username) {

        // Retrieve User details
        String jsonObj = userSvc.getUserObjInString(username);

        return new ResponseEntity<>(jsonObj, HttpStatusCode.valueOf(200));
    }
}
