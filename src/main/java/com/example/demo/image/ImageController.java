package com.example.demo.image;

import com.example.demo.config.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    //为指定用户添加图片
    @PostMapping("users/{userId}/image")
    public ResponseEntity<String> uploadImage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token exception");
        }
        jwtToken = authHeader.substring(7);
        if (!userRepository.findByEmail(jwtService.extractUsername(jwtToken))
                .orElse(new User()).getId().equals(userId)){
            throw new RuntimeException("No access permission");
        }
        return ResponseEntity.ok(imageService.saveImage(userId, file));
    }

    //根据图片名字拿到图片文件
    @GetMapping("users/{userId}/image/{imageName}")
    public ResponseEntity<ByteArrayResource> getImage1(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId,
            @PathVariable("imageName") String imageName
    ) throws IOException {
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token exception");
        }
        jwtToken = authHeader.substring(7);
        if (!userRepository.findByEmail(jwtService.extractUsername(jwtToken))
                .orElse(new User()).getId().equals(userId)){
            throw new RuntimeException("No access permission");
        }
        MultipartFile file = imageService.getImage(userId, imageName);
        ByteArrayResource resource = new ByteArrayResource(file.getBytes());
        return ResponseEntity.ok(resource);
    }

    //拿到某个用户的所有图片的名字
    @GetMapping("users/{userId}/images")
    public ResponseEntity<ImagesResponse> getImages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId
    ) {
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token exception");
        }
        jwtToken = authHeader.substring(7);
        if (!userRepository.findByEmail(jwtService.extractUsername(jwtToken))
                .orElse(new User()).getId().equals(userId)) {
            throw new RuntimeException("No access permission");
        }
        return ResponseEntity.ok(imageService.getImages(userId));
    }

    //识别图片
    @PostMapping("users/image")
    public ResponseEntity<String> classifyImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile image
    ) {
        return ResponseEntity.ok(imageService.classifyImage(image));
    }
}
