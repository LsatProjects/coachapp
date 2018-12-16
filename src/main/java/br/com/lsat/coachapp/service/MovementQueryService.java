package br.com.lsat.coachapp.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import br.com.lsat.coachapp.domain.Movement;
import br.com.lsat.coachapp.domain.*; // for static metamodels
import br.com.lsat.coachapp.repository.MovementRepository;
import br.com.lsat.coachapp.repository.search.MovementSearchRepository;
import br.com.lsat.coachapp.service.dto.MovementCriteria;

/**
 * Service for executing complex queries for Movement entities in the database.
 * The main input is a {@link MovementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Movement} or a {@link Page} of {@link Movement} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MovementQueryService extends QueryService<Movement> {

    private final Logger log = LoggerFactory.getLogger(MovementQueryService.class);

    private final MovementRepository movementRepository;

    private final MovementSearchRepository movementSearchRepository;

    public MovementQueryService(MovementRepository movementRepository, MovementSearchRepository movementSearchRepository) {
        this.movementRepository = movementRepository;
        this.movementSearchRepository = movementSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Movement} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Movement> findByCriteria(MovementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Movement> specification = createSpecification(criteria);
        return movementRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Movement} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Movement> findByCriteria(MovementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Movement> specification = createSpecification(criteria);
        return movementRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MovementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Movement> specification = createSpecification(criteria);
        return movementRepository.count(specification);
    }

    /**
     * Function to convert MovementCriteria to a {@link Specification}
     */
    private Specification<Movement> createSpecification(MovementCriteria criteria) {
        Specification<Movement> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Movement_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Movement_.name));
            }
            if (criteria.getAbbreviation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAbbreviation(), Movement_.abbreviation));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), Movement_.note));
            }
            if (criteria.getMovementCategoryId() != null) {
                specification = specification.and(buildSpecification(criteria.getMovementCategoryId(),
                    root -> root.join(Movement_.movementCategory, JoinType.LEFT).get(MovementCategory_.id)));
            }
        }
        return specification;
    }
}
