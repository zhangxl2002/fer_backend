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
    @PostMapping("users/{userId}/image")
    public ResponseEntity<String> uploadImage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file
//            @RequestParam("images") MultipartFile[] images
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


//    @GetMapping("users/{userId}/image/{imageName}")
//    public @ResponseBody byte[] getImage(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("userId") Long userId,
//            @PathVariable("imageName") String imageName
//    ) throws IOException {
//        InputStream in = getClass()
//                .getResourceAsStream("/com/baeldung/produceimage/image.jpg");
//        return IOUtils.toByteArray(in);
//    }
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
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        MultiValueMap<String, Object> body= new LinkedMultiValueMap<>();

        MultipartFile file = imageService.getImage(userId, imageName);
        ByteArrayResource resource = new ByteArrayResource(file.getBytes());
//        body.add("circularAttachment", new ByteArrayResource(file.getBytes()));

        return ResponseEntity.ok(resource);
    }
//    @GetMapping("users/{userId}/image/{imageId}")
//    public ResponseEntity<MultipartFile> getImage(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("userId") Long userId,
//            @PathVariable("imageId") Long imageId
//    ) {
//        final String jwtToken;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Token exception");
//        }
//        jwtToken = authHeader.substring(7);
//        if (!userRepository.findByEmail(jwtService.extractUsername(jwtToken))
//                .orElse(new User()).getId().equals(userId)){
//            throw new RuntimeException("No access permission");
//        }
//        return ResponseEntity.ok(imageService.getImage(userId, imageId));
//    }

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

    @PostMapping("users/image")
    public ResponseEntity<String> classifyImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile image
    ) {
        return ResponseEntity.ok(imageService.classifyImage(image));
    }
}
