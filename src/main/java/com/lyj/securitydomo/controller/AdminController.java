package com.lyj.securitydomo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/admin_index")
    public String list() {
        return "adminIndex";  // 뷰 이름을 문자열로 반환
    }
}