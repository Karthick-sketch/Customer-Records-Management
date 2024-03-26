package com.customerrecordsmanagement.csvfiledetail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "csv_file_details")
public class CsvFileDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private Long accountId;
    @NonNull
    private String fileName;
    @NonNull
    private String filePath;
}
