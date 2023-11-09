package com.example.demo.image;

import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    public String saveImage(Long userId, MultipartFile file)  {
        // 这里没有保持文件操作和数据库操作的原子性

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
        Image newImage = Image.builder()
                .name(fileName)
                .path(filePath)
                .userId(userId) // 设置所属用户的ID
                .build();
        imageRepository.save(newImage);
        return "File upload succeeded";
    }
    public String classifyImage(MultipartFile image) {
        // 调用python提供的人脸识别接口
        return "";
    }

    public MultipartFile getImage(Long userId, String imageName) {
        Image image = imageRepository.findByNameAndUserId(imageName, userId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Image: %s not found,userId: %d", imageName,userId)
                ));
        String path =  image.getPath();
        Path filePath = Paths.get(path);
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            byte[] fileBytes;
            try {
                fileBytes = Files.readAllBytes(filePath);
            } catch(IOException e) {
                throw new RuntimeException("Failed to read file");
            }
            MultipartFile file = new MockMultipartFile(filePath.getFileName().toString(), fileBytes);
            return file;
        } else {
            throw new RuntimeException("File not found or is not a regular file");
        }
    }

    public ImagesResponse getImages(Long userId) {
        List<Image> imageList = imageRepository.findByUserId(userId);
        List<String> nameList = imageList.stream()
                .map(Image::getName) // 提取每个 Image 对象的 name 字段
                .collect(Collectors.toList()); // 将提取的结果收集到列表中
        return ImagesResponse.builder()
                .imageList(nameList.toString())
                .build();
    }
}
