package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.HolidayModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface HolidayRepository extends JpaRepository<HolidayModel, Long> {
    @Query("SELECT h FROM HolidayModel h WHERE YEAR(h.holidayStart) = :year")
    List<HolidayModel> findByYear(@Param("year") Integer year);

    @Query(value = "SELECT * FROM holidays WHERE YEAR(hday_str_date) = :year", nativeQuery = true)
    List<HolidayModel> findByYearY(@Param("year") Integer year);

}
