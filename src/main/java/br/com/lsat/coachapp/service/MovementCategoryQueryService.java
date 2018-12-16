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

import br.com.lsat.coachapp.domain.MovementCategory;
import br.com.lsat.coachapp.domain.*; // for static metamodels
import br.com.lsat.coachapp.repository.MovementCategoryRepository;
import br.com.lsat.coachapp.repository.search.MovementCategorySearchRepository;
import br.com.lsat.coachapp.service.dto.MovementCategoryCriteria;

/**
 * Service for executing complex queries for MovementCategory entities in the database.
 * The main input is a {@link MovementCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MovementCategory} or a {@link Page} of {@link MovementCategory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MovementCategoryQueryService extends QueryService<MovementCategory> {

    private final Logger log = LoggerFactory.getLogger(MovementCategoryQueryService.class);

    private final MovementCategoryRepository movementCategoryRepository;

    private final MovementCategorySearchRepository movementCategorySearchRepository;

    public MovementCategoryQueryService(MovementCategoryRepository movementCategoryRepository, MovementCategorySearchRepository movementCategorySearchRepository) {
        this.movementCategoryRepository = movementCategoryRepository;
        this.movementCategorySearchRepository = movementCategorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link MovementCategory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MovementCategory> findByCriteria(MovementCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MovementCategory> specification = createSpecification(criteria);
        return movementCategoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link MovementCategory} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MovementCategory> findByCriteria(MovementCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MovementCategory> specification = createSpecification(criteria);
        return movementCategoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MovementCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MovementCategory> specification = createSpecification(criteria);
        return movementCategoryRepository.count(specification);
    }

    /**
     * Function to convert MovementCategoryCriteria to a {@link Specification}
     */
    private Specification<MovementCategory> createSpecification(MovementCategoryCriteria criteria) {
        Specification<MovementCategory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), MovementCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MovementCategory_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), MovementCategory_.description));
            }
            if (criteria.getSportId() != null) {
                specification = specification.and(buildSpecification(criteria.getSportId(),
                    root -> root.join(MovementCategory_.sport, JoinType.LEFT).get(Sport_.id)));
            }
        }
        return specification;
    }
}
