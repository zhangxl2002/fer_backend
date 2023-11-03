package com.example.demo.image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByIdAndUserId(Long id, Long userId);

    Optional<Image> findByNameAndUserId(String name, Long userId);

    List<Image> findByUserId(Long userId);
}
