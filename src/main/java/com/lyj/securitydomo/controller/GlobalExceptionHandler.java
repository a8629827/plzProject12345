package com.lyj.securitydomo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    // 404 오류를 처리하는 메서드
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundError(Model model, Exception ex) {
        // 404 오류 발생 시 로그 출력
        log.error("404 Not Found: " + ex.getMessage());

        // 오류 메시지를 모델에 추가
        model.addAttribute("error", "요청한 페이지를 찾을 수 없습니다.");
        return "error/404"; // 404 오류 페이지로 포워딩
    }
}
