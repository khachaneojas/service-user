package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.ToDoModel;
import com.sprk.commons.entity.primary.user.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface ToDoRepository extends JpaRepository<ToDoModel, Long> {
    ToDoModel findByToDoUid(String uid);
    Boolean existsByToDoUid(String toDoUid);
    List<ToDoModel> findByCreatedBy(UserModel userModel);
}
