package com.lyj.securitydomo.controller;


import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.upload.UploadFileDTO;

import com.lyj.securitydomo.dto.upload.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Controller
@Log4j2
public class UpDownController {

    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;




    @GetMapping("/view/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/remove")
    public String removeFile(@RequestParam("fileName") String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //섬네일이 존재한다면
            if (contentType.startsWith("image")) {
                String fileName1=fileName.replace("s_","");
                File originalFile = new File(uploadPath + File.separator + fileName1);
                originalFile.delete();

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);
        return "/posting/list";
    }

}