package com.sprk.service.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sprk.commons.document.dto.MainTab;
import com.sprk.commons.entity.primary.user.tag.EmployeeRequestStatus;
import com.sprk.service.user.dto.response.common.ExperienceResponse;
import com.sprk.service.user.util.serializer.CDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;





@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EEmployeeRequestResponse {
	@JsonSerialize(using = CDateSerializer.class)
	private Instant joined_at;

	private String req_id;
	private String emp_id;
	private String firstname;
	private String middlename;
	private String lastname;

	@JsonSerialize(using = CDateSerializer.class)
	private Instant birth_date;
	private String phone;
	private String alt_phone;
	private String gender;
	private String email;
	private String alt_email;
	private EmployeeRequestStatus request_status;
	private String blood_group;
	private String marital_status;
	private String nationality;
	private String current_flat_house;
	private String current_building_apartment;
	private String current_area_street_sector_village;
	private String current_landmark;
	private String current_pin_code;
	private String current_city_town;
	private String current_state;
	private String current_country;
	private String permanent_flat_house;
	private String permanent_building_apartment;
	private String permanent_area_street_sector_village;
	private String permanent_landmark;
	private String permanent_pin_code;
	private String permanent_city_town;
	private String permanent_state;
	private String permanent_country;
	private String university;
	private String degree;
	private String stream;
	private String college;
	private String cgpa;
	private String passing_year;
	private Set<String> skill_set = new HashSet<>();
	private Set<ExperienceResponse> experience;
	private String eme_name;
	private String eme_phone;
	private String eme_relation;
	private String physician_name;
	private String physician_phone;
	private String medical_conditions;
	private String doc_identity;
	private String doc_address;
	private String doc_offer_letter;
	private String doc_education;
	private String doc_experience;
	private String doc_salary_slip;
	private Set<String> authorities = new HashSet<>();
	private Set<MainTab> entitlements = new HashSet<>();
}
