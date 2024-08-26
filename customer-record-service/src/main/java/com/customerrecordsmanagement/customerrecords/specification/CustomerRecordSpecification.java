package com.customerrecordsmanagement.customerrecords.specification;

import com.customerrecordsmanagement.customerrecords.entity.CustomerRecord;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class CustomerRecordSpecification {
    public static Specification<CustomerRecord> withAccountIdAndSearchQuery(long accountId, String search) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("email"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("first_name"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("last_name"), "%" + accountId + "%"));
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("accountId"), accountId), predicate);
        });
    }
}
