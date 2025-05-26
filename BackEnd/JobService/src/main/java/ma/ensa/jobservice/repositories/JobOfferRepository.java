package ma.ensa.jobservice.repositories;
import ma.ensa.jobservice.entities.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByRecruiterId(Long recruiterId);

    @Query("SELECT j FROM JobOffer j ORDER BY j.postedAt DESC")
    List<JobOffer> findAllActiveJobOffers();

    @Query("SELECT j FROM JobOffer j WHERE j.title LIKE %:keyword% OR j.description LIKE %:keyword%")
    List<JobOffer> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT j FROM JobOffer j WHERE :technology MEMBER OF j.requiredTechnologies")
    List<JobOffer> findByTechnology(@Param("technology") String technology);
}
