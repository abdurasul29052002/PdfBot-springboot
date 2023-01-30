package com.example.pdfbotspringboot.repository;

import com.example.pdfbotspringboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUserId(Long userId);

    Optional<User> findByUserId(Long userId);
    Integer countAllByActive(Boolean active);
//    @Query(value = "select u.id, u.user_name, u.user_id, u.botstate, u.language_user, u.active, u.invited_by_id, (select count(u2.id) from users u2 where u2.invited_by_id=u.id) as referralCount from users u order by referralCount desc limit :size", nativeQuery = true)
//    List<User> findAllByCountReferralDesc(int size);
    Integer countAllByInvitedById(Long invitedBy_id);
}
