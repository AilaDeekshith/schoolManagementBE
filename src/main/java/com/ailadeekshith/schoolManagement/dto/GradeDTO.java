package com.ailadeekshith.schoolManagement.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDTO {
    private Long id;
    private String name;
    private Integer displayOrder;
    private List<SectionDTO> sections;
}
