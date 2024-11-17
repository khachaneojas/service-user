package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.UserModel;
import com.sprk.commons.entity.primary.user.mapping.NotificationUserMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface NotificationUserMappingRepository extends JpaRepository<NotificationUserMapping, Long> {
    NotificationUserMapping findByNotificationNotificationIdAndUserUserPid(Long notificationId, Long userPid);
    boolean existsByNotificationNotificationIdAndUserUserPid(Long notificationId, Long userPid);
    boolean existsByNotificationNotificationIdAndUserUserUid(Long notificationId, String userUid);
    boolean existsByNotificationNotificationIdAndUser(Long notificationId, UserModel userModel);

    List<NotificationUserMapping> findByUserUserPid(Long userPid);
    List<NotificationUserMapping> findTop5ByUserUserPid(Long userPid);

    Long countByUserUserPidAndSeenFalse(Long userPid);
    Long countByUserUserPid(Long userPid);
    Page<NotificationUserMapping> findByUserUserPidAndSeen(Long userPid, boolean seen ,Pageable pageable);
    Page<NotificationUserMapping> findByUserUserPid(Long userPid,Pageable pageable);
}
