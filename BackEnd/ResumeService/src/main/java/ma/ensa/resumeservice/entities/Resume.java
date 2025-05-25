package ma.ensa.resumeservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    private Long applicationId;

    @Column(unique = true, length = 64)
    private String fileHash;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @Lob
    private byte[] fileData;

    @ElementCollection
    @CollectionTable(name = "resume_extracted_skills",
            joinColumns = @JoinColumn(name = "resume_id"))
    private List<String> extractedSkills = new ArrayList<>();

    // FIX: Use proper JSONB handling for PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_data", columnDefinition = "jsonb")
    private String parsedData;

    private LocalDateTime uploadedAt;
}