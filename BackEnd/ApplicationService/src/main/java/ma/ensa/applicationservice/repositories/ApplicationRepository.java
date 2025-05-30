package ma.ensa.applicationservice.repositories;

import org.springframework.stereotype.Repository;
import ma.ensa.applicationservice.entities.Application;
import ma.ensa.applicationservice.entities.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByJobOfferId(Long jobOfferId);

    List<Application> findByCandidateIdAndStatus(Long candidateId, ApplicationStatus status);

    List<Application> findByJobOfferIdAndStatus(Long jobOfferId, ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.candidateId = :candidateId AND a.jobOfferId = :jobOfferId")
    Optional<Application> findByCandidateIdAndJobOfferId(@Param("candidateId") Long candidateId, @Param("jobOfferId") Long jobOfferId);

    @Query("SELECT a FROM Application a ORDER BY a.appliedAt DESC")
    List<Application> findAllOrderByAppliedAtDesc();

    @Query("SELECT a FROM Application a WHERE a.status = :status ORDER BY a.appliedAt DESC")
    List<Application> findByStatusOrderByAppliedAtDesc(@Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.candidateId = :candidateId")
    Long countByCandidateId(@Param("candidateId") Long candidateId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobOfferId = :jobOfferId")
    Long countByJobOfferId(@Param("jobOfferId") Long jobOfferId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.candidateId = :candidateId AND a.status = :status")
    Long countByCandidateIdAndStatus(@Param("candidateId") Long candidateId, @Param("status") ApplicationStatus status);
}
