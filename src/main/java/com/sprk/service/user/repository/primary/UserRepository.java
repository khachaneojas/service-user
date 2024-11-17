package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface UserRepository extends JpaRepository<UserModel, Long> {
	List<UserModel> findAllByUserUidIn(List<String> uIds);
	UserModel findByUserPid(Long userPid);
	UserModel findByUserUid(String userUid);
	UserModel findByEmailOrUserUid(String email, String userUid);
    Boolean existsByEmail(String email);
	long countByEmail(String email);
	long countByPhone(String phone);
	Boolean existsByUserUid(String userUid);
	Long countByUserUid(String userUid);
	Boolean existsByProfileIsNotNullAndUserPid(Long userPid);
	Boolean existsByOrganizationIsNotNullAndUserPid(Long userPid);
}
