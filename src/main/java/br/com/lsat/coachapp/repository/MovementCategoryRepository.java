package br.com.lsat.coachapp.repository;

import br.com.lsat.coachapp.domain.MovementCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MovementCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovementCategoryRepository extends JpaRepository<MovementCategory, Long>, JpaSpecificationExecutor<MovementCategory> {

}
