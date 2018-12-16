package br.com.lsat.coachapp.service;

import br.com.lsat.coachapp.domain.MovementCategory;
import br.com.lsat.coachapp.repository.MovementCategoryRepository;
import br.com.lsat.coachapp.repository.search.MovementCategorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MovementCategory.
 */
@Service
@Transactional
public class MovementCategoryService {

    private final Logger log = LoggerFactory.getLogger(MovementCategoryService.class);

    private final MovementCategoryRepository movementCategoryRepository;

    private final MovementCategorySearchRepository movementCategorySearchRepository;

    public MovementCategoryService(MovementCategoryRepository movementCategoryRepository, MovementCategorySearchRepository movementCategorySearchRepository) {
        this.movementCategoryRepository = movementCategoryRepository;
        this.movementCategorySearchRepository = movementCategorySearchRepository;
    }

    /**
     * Save a movementCategory.
     *
     * @param movementCategory the entity to save
     * @return the persisted entity
     */
    public MovementCategory save(MovementCategory movementCategory) {
        log.debug("Request to save MovementCategory : {}", movementCategory);
        MovementCategory result = movementCategoryRepository.save(movementCategory);
        movementCategorySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the movementCategories.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MovementCategory> findAll() {
        log.debug("Request to get all MovementCategories");
        return movementCategoryRepository.findAll();
    }


    /**
     * Get one movementCategory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<MovementCategory> findOne(Long id) {
        log.debug("Request to get MovementCategory : {}", id);
        return movementCategoryRepository.findById(id);
    }

    /**
     * Delete the movementCategory by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MovementCategory : {}", id);
        movementCategoryRepository.deleteById(id);
        movementCategorySearchRepository.deleteById(id);
    }

    /**
     * Search for the movementCategory corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<MovementCategory> search(String query) {
        log.debug("Request to search MovementCategories for query {}", query);
        return StreamSupport
            .stream(movementCategorySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
