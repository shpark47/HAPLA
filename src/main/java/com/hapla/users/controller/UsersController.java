package com.hapla.users.controller;

import com.google.gson.JsonObject;
import com.hapla.exception.Exception;
import com.hapla.users.model.service.UsersService;
import com.hapla.users.model.vo.Users;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/checkUser")
    @ResponseBody
    public String checkUser(@RequestBody Users user, HttpSession session) {
        int checkResult = usersService.checkUser(user);
        Users u = usersService.login(user);
        JSONObject json = new JSONObject();
        json.put("checkResult", checkResult);
        json.put("user", u);

        session.setAttribute("loginUser", u);

        return json.toString();
    }

    @PostMapping("/join")
    @ResponseBody
    public String join(@RequestBody Users user) {
        System.out.println(user);
        int result = usersService.join(user);
        if (result == 1) {
            JSONObject json = new JSONObject();
            json.put("success", true);
            return json.toString();
        }
        throw new Exception("회원가입에 실패하였습니다.");
    }

    @GetMapping("/users/logout")
    public String logout(SessionStatus session) {
        session.setComplete();
        return "redirect:/main";
    }

    private static final String UPLOAD_DIR = "c:/profiles/"; // 파일 저장 경로

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String renameFileName = new SimpleDateFormat("yyyyMMddHHmmssSSS")
                    .format(new Date()) + (int)(Math.random() * 100000) + extension;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(renameFileName);
            image.transferTo(filePath.toFile());

            if (!Files.exists(filePath)) {
                throw new IOException("파일 저장 실패: " + filePath);
            }
            System.out.println("파일 저장 성공: " + filePath);

            String imagePath = "/profiles/" + renameFileName; // 클라이언트가 사용할 경로
            Map<String, String> response = new HashMap<>();
            response.put("imagePath", imagePath);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/checkNickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname) {
        int result = usersService.checkNickname(nickname);
        JSONObject json = new JSONObject();
        if (result > 0) {
            json.put("result", false);
        }else{
            json.put("result", true);
        }
        return json.toString();
    }
}
