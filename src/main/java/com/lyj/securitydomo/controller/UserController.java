package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.UserRepository;
import com.lyj.securitydomo.service.PostService;
import com.lyj.securitydomo.service.PostServiceImpl;
import com.lyj.securitydomo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/join")
    public void join(){
    }

    @PostMapping("register")
    public String register(User user) {
        System.out.println("회원가입 진행 : " + user);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        user.setRole("USER");
        userRepository.save(user);
        return "redirect:/user/login";
    }
    @GetMapping("login")
    public void login(){
    }
    // 마이페이지 정보 조회 (GET 요청)
    @GetMapping("/mypage")
    public String getMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model) {

        log.info("mypage");
        // 현재 로그인한 사용자 정보를 가져옵니다.
        User user = principal.getUser(); // 현재 로그인한 사용자의 정보를 가져오는 메소드 (로그인 처리 방식에 따라 달라질 수 있습니다.)
        log.info("user"+ user);
        // 모델에 사용자 정보를 추가하여 뷰로 전달합니다.
        model.addAttribute("user", user);

        // 마이페이지 HTML 뷰를 반환합니다.
        return "/user/mypage";
    }

    // 사용자 정보 수정 처리 (POST 요청)
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        User existingUser = userRepository.findById(user.getId()).orElseThrow();
        // 비밀번호가 입력되지 않았다면 기존 비밀번호 유지
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            // 비밀번호가 입력되었다면 새 비밀번호로 설정 (암호화)
            String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encPassword);
        }
        user.setRole(existingUser.getRole());  // 기존 역할 유지

        // 사용자가 수정한 정보로 사용자 업데이트
        userService.save(user);

        // 업데이트 후 마이페이지로 리다이렉트
        return "redirect:/user/mypage";
    }

    @GetMapping("/readmypage")
    public String readMyPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        // 필요한 데이터가 있다면 모델에 추가
        return "user/readmypage"; // readmypage.html 뷰로 이동
    }

    @GetMapping("/info")
    public String getUserInfo(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        // 로그인한 사용자 정보 가져오기
        User user = principal.getUser();

        // 모델에 사용자 정보 추가
        model.addAttribute("user", user);

        // 사용자 정보 페이지로 이동
        return "user/info";
    }
}
