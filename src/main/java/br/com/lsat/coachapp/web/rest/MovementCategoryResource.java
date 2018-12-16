package br.com.lsat.coachapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.lsat.coachapp.domain.MovementCategory;
import br.com.lsat.coachapp.service.MovementCategoryService;
import br.com.lsat.coachapp.web.rest.errors.BadRequestAlertException;
import br.com.lsat.coachapp.web.rest.util.HeaderUtil;
import br.com.lsat.coachapp.service.dto.MovementCategoryCriteria;
import br.com.lsat.coachapp.service.MovementCategoryQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing MovementCategory.
 */
@RestController
@RequestMapping("/api")
public class MovementCategoryResource {

    private final Logger log = LoggerFactory.getLogger(MovementCategoryResource.class);

    private static final String ENTITY_NAME = "movementCategory";

    private final MovementCategoryService movementCategoryService;

    private final MovementCategoryQueryService movementCategoryQueryService;

    public MovementCategoryResource(MovementCategoryService movementCategoryService, MovementCategoryQueryService movementCategoryQueryService) {
        this.movementCategoryService = movementCategoryService;
        this.movementCategoryQueryService = movementCategoryQueryService;
    }

    /**
     * POST  /movement-categories : Create a new movementCategory.
     *
     * @param movementCategory the movementCategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new movementCategory, or with status 400 (Bad Request) if the movementCategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/movement-categories")
    @Timed
    public ResponseEntity<MovementCategory> createMovementCategory(@Valid @RequestBody MovementCategory movementCategory) throws URISyntaxException {
        log.debug("REST request to save MovementCategory : {}", movementCategory);
        if (movementCategory.getId() != null) {
            throw new BadRequestAlertException("A new movementCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MovementCategory result = movementCategoryService.save(movementCategory);
        return ResponseEntity.created(new URI("/api/movement-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /movement-categories : Updates an existing movementCategory.
     *
     * @param movementCategory the movementCategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated movementCategory,
     * or with status 400 (Bad Request) if the movementCategory is not valid,
     * or with status 500 (Internal Server Error) if the movementCategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/movement-categories")
    @Timed
    public ResponseEntity<MovementCategory> updateMovementCategory(@Valid @RequestBody MovementCategory movementCategory) throws URISyntaxException {
        log.debug("REST request to update MovementCategory : {}", movementCategory);
        if (movementCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MovementCategory result = movementCategoryService.save(movementCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, movementCategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /movement-categories : get all the movementCategories.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of movementCategories in body
     */
    @GetMapping("/movement-categories")
    @Timed
    public ResponseEntity<List<MovementCategory>> getAllMovementCategories(MovementCategoryCriteria criteria) {
        log.debug("REST request to get MovementCategories by criteria: {}", criteria);
        List<MovementCategory> entityList = movementCategoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
    * GET  /movement-categories/count : count all the movementCategories.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/movement-categories/count")
    @Timed
    public ResponseEntity<Long> countMovementCategories(MovementCategoryCriteria criteria) {
        log.debug("REST request to count MovementCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(movementCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /movement-categories/:id : get the "id" movementCategory.
     *
     * @param id the id of the movementCategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the movementCategory, or with status 404 (Not Found)
     */
    @GetMapping("/movement-categories/{id}")
    @Timed
    public ResponseEntity<MovementCategory> getMovementCategory(@PathVariable Long id) {
        log.debug("REST request to get MovementCategory : {}", id);
        Optional<MovementCategory> movementCategory = movementCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(movementCategory);
    }

    /**
     * DELETE  /movement-categories/:id : delete the "id" movementCategory.
     *
     * @param id the id of the movementCategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/movement-categories/{id}")
    @Timed
    public ResponseEntity<Void> deleteMovementCategory(@PathVariable Long id) {
        log.debug("REST request to delete MovementCategory : {}", id);
        movementCategoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/movement-categories?query=:query : search for the movementCategory corresponding
     * to the query.
     *
     * @param query the query of the movementCategory search
     * @return the result of the search
     */
    @GetMapping("/_search/movement-categories")
    @Timed
    public List<MovementCategory> searchMovementCategories(@RequestParam String query) {
        log.debug("REST request to search MovementCategories for query {}", query);
        return movementCategoryService.search(query);
    }

}
