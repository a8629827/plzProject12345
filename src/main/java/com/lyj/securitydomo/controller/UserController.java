package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.UserRepository;
import com.lyj.securitydomo.service.PostService;
import com.lyj.securitydomo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public void join() {
    }

    @PostMapping("/register")
    public String register(User user, RedirectAttributes redirectAttributes) {
        log.info("회원가입 진행 : " + user);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        user.setRole("USER");
        userRepository.save(user);

        // 회원가입 완료 메시지 추가
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");

        return "redirect:/user/join";
    }

    @GetMapping("/login")
    public void login() {
    }

    // 마이페이지 정보 조회
    @GetMapping("/mypage")
    public String getMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        log.info("mypage");

        User user = principal.getUser();
        log.info("user: " + user);
        model.addAttribute("user", user);

        return "/user/mypage";
    }

    // 사용자 정보 수정
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElseThrow();

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encPassword);
        }
        user.setRole(existingUser.getRole());

        userService.save(user);

        return "redirect:/user/mypage";
    }

    // 마이페이지 읽기
    @GetMapping("/readmypage")
    public String readMyPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        return "/user/readmypage";
    }
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        model.addAttribute("user", principal.getUser());
        return "/user/info";
    }

    // 회원 탈퇴 기능 추가
    @PostMapping("/delete")
    public String deleteUser(@AuthenticationPrincipal PrincipalDetails principal) {
        User user = principal.getUser();
        userService.deleteUser(user.getUserId()); // 필드명이 userId일 경우
        return "redirect:/user/logout"; // 로그아웃 후 메인 페이지로 이동
    }
    @GetMapping("/mywriting")
    public String myWritinglist(PageRequestDTO pageRequestDTO, @AuthenticationPrincipal PrincipalDetails principal, Model model) {
        if (pageRequestDTO.getSize() <= 0) {
            pageRequestDTO.setSize(10); // 기본값 설정
        }

        // 게시글 목록을 가져올 때, isVisible이 true인 게시글만 필터링
        PageResponseDTO<PostDTO> responseDTO = postService.writinglist(pageRequestDTO, principal.getUser());

        // 모델에 게시글을 추가하기 전에 로그 출력
        log.info("게시글 목록 전달: {}", responseDTO.getDtoList());

        model.addAttribute("myPosts", responseDTO.getDtoList()); // 게시글 DTO 리스트 추가
        model.addAttribute("totalPages", (int) Math.ceil(responseDTO.getTotal() / (double) pageRequestDTO.getSize())); // 총 페이지 수 계산
        model.addAttribute("currentPage", responseDTO.getPage()); //
        model.addAttribute("user", principal.getUser());
        return "/user/mywriting";
    }
    /**
     * 게시글 읽기 및 수정 페이지를 보여주는 메서드
     */
    @GetMapping("/readwriting/{postId}")
    public String read(@PathVariable Long postId, Model model) {
        PostDTO postDTO = postService.readOne(postId);
        log.info(postDTO);
        model.addAttribute("post", postDTO);
        model.addAttribute("originalImages", postDTO.getOriginalImageLinks()); // 이미지 링크 추가
        return "/user/readwriting"; // 상세보기 페이지
    }
}