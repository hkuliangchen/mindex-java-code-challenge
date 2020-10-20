package com.mindex.challenge.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;
}
