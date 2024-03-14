package com.customerrecordsmanagement.config;

import com.customerrecordsmanagement.customerrecords.CustomerRecord;
import com.customerrecordsmanagement.customfields.CustomField;
import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerRecordMapper implements RowMapper<CustomerRecord> {
    @Override
    public CustomerRecord mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setId(rs.getLong("id"));
        customerRecord.setAccountId(rs.getLong("account_id"));
        customerRecord.setEmail(rs.getString("email"));
        customerRecord.setFirstName(rs.getString("first_name"));
        customerRecord.setLastName(rs.getString("last_name"));
        customerRecord.setPhoneNumber(rs.getString("phone_number"));
        customerRecord.setCompanyName(rs.getString("company_name"));
        customerRecord.setAddress(rs.getString("address"));
        customerRecord.setCity(rs.getString("city"));
        customerRecord.setState(rs.getString("state"));
        customerRecord.setCountry(rs.getString("country"));
        customerRecord.setZipcode(rs.getInt("zipcode"));

        customerRecord.setCustomField(mapCustomField(rs));

        return customerRecord;
    }

    private CustomField mapCustomField(@NonNull ResultSet rs) throws SQLException {
        CustomField customField = new CustomField();
        customField.setField1(rs.getString("field1"));
        customField.setField2(rs.getString("field2"));
        customField.setField3(rs.getString("field3"));
        customField.setField4(rs.getString("field4"));
        customField.setField5(rs.getString("field5"));
        customField.setField6(rs.getString("field6"));
        customField.setField7(rs.getString("field7"));
        customField.setField8(rs.getString("field8"));
        customField.setField9(rs.getString("field9"));
        customField.setField10(rs.getString("field10"));

        return customField;
    }
}
