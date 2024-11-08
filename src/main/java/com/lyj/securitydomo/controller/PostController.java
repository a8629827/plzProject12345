package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.upload.UploadFileDTO;
import com.lyj.securitydomo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/posting")
@RequiredArgsConstructor
public class PostController {

    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;

    private final PostService postService;

    /**
     * 게시글 목록을 조회하고 모델에 전달하는 메서드
     *
     * @return
     */
    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<PostDTO> responseDTO = postService.list(pageRequestDTO);
       // log.info(responseDTO);
        model.addAttribute("posts", responseDTO.getDtoList());
        return "posting/list";
    }

    /**
     * 게시글 등록 페이지를 보여주는 메서드
     */
    @GetMapping("/register")
    public void registerGET() {}

    /**
     * 게시글 등록을 처리하는 메서드
     */
    @PostMapping("/register")
    public String registerPost(UploadFileDTO uploadFileDTO, PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        try {
            List<String> fileNames = uploadFiles(uploadFileDTO);
            postDTO.setFileNames(fileNames);
            Long postId = postService.register(postDTO);
            redirectAttributes.addFlashAttribute("result", postId);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "이미지 파일만 업로드 가능합니다.");
            return "redirect:/posting/register"; // 등록 페이지로 리디렉션
        }
        return "redirect:/posting/list";
    }

    /**
     * 게시글 읽기 및 수정 페이지를 보여주는 메서드
     */
    @GetMapping("/read/{postId}")
    public String read(@PathVariable Long postId, Model model) {
        PostDTO postDTO = postService.readOne(postId);
        log.info(postDTO);
        model.addAttribute("post", postDTO);
        model.addAttribute("originalImages", postDTO.getOriginalImageLinks()); // 이미지 링크 추가
        return "posting/read"; // 상세보기 페이지
    }

    /**
     * 게시글 수정 처리를 위한 메서드
     */
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO, UploadFileDTO uploadFileDTO, @Valid PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());
            return "redirect:/posting/modify" + pageRequestDTO.getLink();
        }

        List<String> fileNames = uploadFiles(uploadFileDTO);

        postService.modify(postDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());

        return "redirect:/posting/read";
    }

    /**
     * 파일 업로드 처리 메서드
     * @param uploadFileDTO 업로드할 파일 정보
     * @return 파일 이름 리스트
     */
    private List<String> uploadFiles(UploadFileDTO uploadFileDTO) throws IOException {
        List<String> fileNames = new ArrayList<>();

        if (uploadFileDTO.getFiles() != null) {
            for (MultipartFile file : uploadFileDTO.getFiles()) {
                String originalName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                // 파일을 지정된 경로에 저장
                file.transferTo(savePath);

                // 이미지 파일인지 확인
                String contentType = Files.probeContentType(savePath);
                if (contentType != null && contentType.startsWith("image")) {
                    // 이미지 파일일 경우 썸네일 생성
                    File thumbnailFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 200, 200);
                    fileNames.add("s_" + uuid + "_" + originalName); // 썸네일 이름 추가
                    fileNames.add(uuid + "_" + originalName); // 원본 파일 이름 추가
                } else {
                    // 이미지 파일이 아닐 경우 예외 발생
                    throw new IOException("Uploaded file is not an image: " + originalName);
                }
            }
        }
        return fileNames;
    }

    /**
     * 파일을 뷰로 보여주는 메서드
     * @param fileName 파일 이름
     * @return 파일에 대한 ResponseEntity
     */
    @GetMapping("/view/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}