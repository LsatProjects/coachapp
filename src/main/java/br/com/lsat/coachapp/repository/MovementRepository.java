package br.com.lsat.coachapp.repository;

import br.com.lsat.coachapp.domain.Movement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Movement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovementRepository extends JpaRepository<Movement, Long>, JpaSpecificationExecutor<Movement> {

}
