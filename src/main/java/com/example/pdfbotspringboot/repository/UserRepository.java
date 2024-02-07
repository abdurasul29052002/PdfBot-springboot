package com.example.pdfbotspringboot.repository;

import com.example.pdfbotspringboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUserId(Long userId);

    Optional<User> findByUserId(Long userId);
    Integer countAllByActive(Boolean active);
    Integer countAllByInvitedById(Long invitedBy_id);
}
