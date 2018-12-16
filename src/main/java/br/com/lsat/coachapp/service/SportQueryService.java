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

import br.com.lsat.coachapp.domain.Sport;
import br.com.lsat.coachapp.domain.*; // for static metamodels
import br.com.lsat.coachapp.repository.SportRepository;
import br.com.lsat.coachapp.repository.search.SportSearchRepository;
import br.com.lsat.coachapp.service.dto.SportCriteria;

/**
 * Service for executing complex queries for Sport entities in the database.
 * The main input is a {@link SportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Sport} or a {@link Page} of {@link Sport} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SportQueryService extends QueryService<Sport> {

    private final Logger log = LoggerFactory.getLogger(SportQueryService.class);

    private final SportRepository sportRepository;

    private final SportSearchRepository sportSearchRepository;

    public SportQueryService(SportRepository sportRepository, SportSearchRepository sportSearchRepository) {
        this.sportRepository = sportRepository;
        this.sportSearchRepository = sportSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Sport} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Sport> findByCriteria(SportCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Sport> specification = createSpecification(criteria);
        return sportRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Sport} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Sport> findByCriteria(SportCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sport> specification = createSpecification(criteria);
        return sportRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SportCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Sport> specification = createSpecification(criteria);
        return sportRepository.count(specification);
    }

    /**
     * Function to convert SportCriteria to a {@link Specification}
     */
    private Specification<Sport> createSpecification(SportCriteria criteria) {
        Specification<Sport> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Sport_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Sport_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Sport_.description));
            }
        }
        return specification;
    }
}
