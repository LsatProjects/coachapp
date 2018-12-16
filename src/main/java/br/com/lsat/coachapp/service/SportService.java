package br.com.lsat.coachapp.service;

import br.com.lsat.coachapp.domain.Sport;
import br.com.lsat.coachapp.repository.SportRepository;
import br.com.lsat.coachapp.repository.search.SportSearchRepository;
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
 * Service Implementation for managing Sport.
 */
@Service
@Transactional
public class SportService {

    private final Logger log = LoggerFactory.getLogger(SportService.class);

    private final SportRepository sportRepository;

    private final SportSearchRepository sportSearchRepository;

    public SportService(SportRepository sportRepository, SportSearchRepository sportSearchRepository) {
        this.sportRepository = sportRepository;
        this.sportSearchRepository = sportSearchRepository;
    }

    /**
     * Save a sport.
     *
     * @param sport the entity to save
     * @return the persisted entity
     */
    public Sport save(Sport sport) {
        log.debug("Request to save Sport : {}", sport);
        Sport result = sportRepository.save(sport);
        sportSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the sports.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Sport> findAll() {
        log.debug("Request to get all Sports");
        return sportRepository.findAll();
    }


    /**
     * Get one sport by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Sport> findOne(Long id) {
        log.debug("Request to get Sport : {}", id);
        return sportRepository.findById(id);
    }

    /**
     * Delete the sport by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sport : {}", id);
        sportRepository.deleteById(id);
        sportSearchRepository.deleteById(id);
    }

    /**
     * Search for the sport corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Sport> search(String query) {
        log.debug("Request to search Sports for query {}", query);
        return StreamSupport
            .stream(sportSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
