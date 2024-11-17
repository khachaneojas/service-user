package com.sprk.service.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.sprk.service.user.dto.SkillSetDTO;
import com.sprk.service.user.dto.response.common.ExperienceResponse;
import com.sprk.service.user.enums.*;
import com.sprk.service.user.util.serializer.CDateSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
	@JsonSerialize(using = CDateSerializer.class)
	private Instant joined_at;

	private String emp_id;
	private String name;
	private String email;
	private String alt_email;
	private String phone;
	private String alt_phone;
	private EmployeeStatus employee_status;
	private Set<String> authorities = new HashSet<>();

	@JsonSerialize(using = CDateSerializer.class)
	private Instant birth_date;
	private String gender;
	private String blood_group;
	private String marital_status;
	private String nationality;
	private String current_address;
	private String permanent_address;
	private String university;
	private String degree;
	private String stream;
	private String college;
	private String cgpa;
	private String passing_year;
	private List<String> skill_set = new ArrayList<>();
	private List<SkillSetDTO> tech_skill = new ArrayList<>();
	private List<String> soft_skill = new ArrayList<>();
	private Set<ExperienceResponse> experience;
	private String eme_name;
	private String eme_phone;
	private String eme_relation;
	private String physician_name;
	private String physician_phone;
	private String medical_conditions;
	private String doc_profile;
	private String doc_identity;
	private String doc_address;
	private String doc_offer_letter;
	private String doc_education;
	private String doc_experience;
	private String doc_salary_slip;
	private boolean is_mon;
	private boolean is_tue;
	private boolean is_wed;
	private boolean is_thu;
	private boolean is_fri;
	private boolean is_sat;
	private boolean is_sun;
	private String in_time;
	private String out_time;
	private int annual_leaves;
}
