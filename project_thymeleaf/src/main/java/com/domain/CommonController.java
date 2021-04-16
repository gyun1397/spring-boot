package com.domain;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommonController {
   
    @GetMapping("/welcome-jsp.do")
    public String welcome() {
        return "welcome";
    }
    
    @GetMapping("/welcome-thymeleaf.do")
    public String welcome(Model model, @RequestParam(required = false) String mssg) {
        model.addAttribute("mssg", mssg);
        return "thymeleaf/welcome";
    }
}
