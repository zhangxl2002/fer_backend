package com.example.demo.image;

import com.example.demo.config.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @PostMapping("/{userId}/image")
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
}