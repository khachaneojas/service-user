package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.common.ActivityModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface ActivityRepository extends JpaRepository<ActivityModel, Long> {
    List<ActivityModel> findByUserUserUid(String userUid);
}
