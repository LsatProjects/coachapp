package br.com.lsat.coachapp.repository;

import br.com.lsat.coachapp.domain.Sport;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Sport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SportRepository extends JpaRepository<Sport, Long>, JpaSpecificationExecutor<Sport> {

}
