package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compensation {

    @JsonIgnore
    @Id
    private String CompensationId;

    private Employee employee;
    private int salary;
    private String effectiveDate;

    private Date effectiveDateBackingVar;

    @Transient
    private String pattern = "MM-dd-yyyy";

    @Transient
    private SimpleDateFormat dateFormat= new SimpleDateFormat(pattern);
}
