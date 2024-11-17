package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.LeaveRequestModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface LeaveRequestRepository extends JpaRepository<LeaveRequestModel, Long> {
	LeaveRequestModel findByLeaveRequestPid(Long userPid);
	LeaveRequestModel findByLeaveRequestUid(String leaveUid);
	Boolean existsByLeaveRequestUid(String leaveUid);
	Long countByLeaveRequestUid(String leaveUid);
	List<LeaveRequestModel> findByAppliedByUserPid(Long userPid);
	List<LeaveRequestModel> findByAppliedByUserUid(String userUid);

	@Query("SELECT l FROM LeaveRequestModel l WHERE l.appliedBy IS NOT NULL")
	List<LeaveRequestModel> findAllByAppliedByIsNotNull();

	@Query("SELECT l FROM LeaveRequestModel l JOIN l.appliedBy u WHERE u IS NOT NULL AND u.userPid = :userPid")
	List<LeaveRequestModel> findAllByAppliedBy(@Param("userPid") Long userPid);
}
