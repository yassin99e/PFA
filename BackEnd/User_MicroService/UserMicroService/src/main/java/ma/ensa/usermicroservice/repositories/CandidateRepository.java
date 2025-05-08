package ma.ensa.usermicroservice.repositories;


import ma.ensa.usermicroservice.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

}
