package com.example.demo.image;

import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final UserRepository repository;
    public String saveImage(Long userId, MultipartFile file)  {
        String uploadDir = "C:\\Users\\Administrator\\Desktop\\SoftwareDesignB\\images";
//
        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + "\\" + userId.toString() + "\\" + fileName;
        File dest = new File(filePath);
        if (dest.exists()) {
            throw new RuntimeException("File already existed");
        }
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs(); // 创建目录及其父目录
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("File save failed");
        }
        return "File upload succeeded";
    }
    public String classifyImage(MultipartFile image) {
        // 调用python提供的人脸识别接口
    }
}
