package ma.ensa.resumeservice.repositories;

import ma.ensa.resumeservice.entities.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByCandidateId(Long candidateId);

    Optional<Resume> findByFileHash(String fileHash);

    List<Resume> findByApplicationId(Long applicationId);

    boolean existsByCandidateIdAndApplicationId(Long candidateId, Long applicationId);
}
