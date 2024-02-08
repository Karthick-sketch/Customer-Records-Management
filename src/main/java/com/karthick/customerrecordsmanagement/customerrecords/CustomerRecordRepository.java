package com.karthick.customerrecordsmanagement.customerrecords;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRecordRepository extends JpaRepository<CustomerRecord, Long> {
//    @Query("INSERT INTO customer_records (COLUMNS) VALUES (...)")
//    CustomerRecord saveIgnore(CustomerRecord customerRecord);
}
