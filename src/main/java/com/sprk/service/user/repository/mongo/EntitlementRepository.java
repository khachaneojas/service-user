package com.sprk.service.user.repository.mongo;


import com.sprk.commons.document.EntitlementModel;

import com.sprk.commons.tag.View;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface EntitlementRepository extends MongoRepository<EntitlementModel, String> {
    EntitlementModel findByRequestUid(String requestUid);
    EntitlementModel findByUserUid(String userUid);
    boolean existsByUserUidAndAuthoritiesIn(String userUid, Collection<String> authorities);
    List<EntitlementModel> findByAuthoritiesIn(Collection<String> authorities);
    List<EntitlementModel> findByUserUidIsNotNullAndEntitlementsSubName(View subTabName);

    @Query(value = "{ 'use_uid': ?0 }", fields = "{ 'authorities': 1, '_id': 0 }")
    EntitlementModel findAuthoritiesByUserUid(String userUid);
}