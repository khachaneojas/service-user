package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.LeaveModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface LeaveRepository extends JpaRepository<LeaveModel, Long> {
	LeaveModel findByLeavePid(Long userPid);
	List<LeaveModel> findByAppliedByUserPid(Long userPid);

	@Procedure(procedureName = "sp_delete_weekly_off_leaves")
	void sp_delete_weekly_off_leaves(
			@Param("emp_id") Long userPid,
			@Param("start_date") LocalDate startDate,
			@Param("end_date") LocalDate endDate
	);

	@Procedure(procedureName = "sp_insert_weekly_off_leaves")
	void sp_insert_weekly_off_leaves(
			@Param("emp_id") Long userPid,
			@Param("start_date") LocalDate startDate,
			@Param("end_date") LocalDate endDate
	);
}
