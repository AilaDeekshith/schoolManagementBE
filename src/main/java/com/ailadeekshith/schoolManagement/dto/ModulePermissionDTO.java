package com.ailadeekshith.schoolManagement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModulePermissionDTO {
    private String module;
    private boolean canRead;
    private boolean canWrite;
}
