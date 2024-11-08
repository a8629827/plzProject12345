package com.lyj.securitydomo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class SampleController {
    @GetMapping("/")
    public String home(){
        log.info("home");
        return "index";
    }
    //@GetMapping("/user/login")
    public void login(){
        log.info("login");
    }
    @GetMapping("/all")
    public String exAll(){
        log.info("exAll");
        return "exAll";
    }
    @GetMapping("/member")
    public void exMember(){

        log.info("exMember");
    }
    @GetMapping("/admin")
    public void exAdmin() {

        log.info("exAdmin");
    }
}
