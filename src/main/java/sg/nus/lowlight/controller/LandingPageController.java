package sg.nus.lowlight.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LandingPageController {
    
    @GetMapping
    public String getIndex() {
        return "index";
    }
}
