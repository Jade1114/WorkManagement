package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TeacherStatsResponse {
    private long pendingSubmissions; 
    private long assignments;        
    private long students;          
    private long courses;            
}
