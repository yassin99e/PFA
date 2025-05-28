package ma.ensa.messagingservice.Repositories;


import ma.ensa.messagingservice.Entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByParticipantOneId(Long participantOneId);
    List<Conversation> findByParticipantTwoId(Long participantTwoId);

    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.participantOneId = :candidateId AND c.participantTwoId = :recruiterId)")
    Optional<Conversation> findByCandidateAndRecruiter(
            @Param("candidateId") Long candidateId,
            @Param("recruiterId") Long recruiterId);
}