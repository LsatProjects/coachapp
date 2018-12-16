package br.com.lsat.coachapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.lsat.coachapp.domain.Movement;
import br.com.lsat.coachapp.service.MovementService;
import br.com.lsat.coachapp.web.rest.errors.BadRequestAlertException;
import br.com.lsat.coachapp.web.rest.util.HeaderUtil;
import br.com.lsat.coachapp.web.rest.util.PaginationUtil;
import br.com.lsat.coachapp.service.dto.MovementCriteria;
import br.com.lsat.coachapp.service.MovementQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing Movement.
 */
@RestController
@RequestMapping("/api")
public class MovementResource {

    private final Logger log = LoggerFactory.getLogger(MovementResource.class);

    private static final String ENTITY_NAME = "movement";

    private final MovementService movementService;

    private final MovementQueryService movementQueryService;

    public MovementResource(MovementService movementService, MovementQueryService movementQueryService) {
        this.movementService = movementService;
        this.movementQueryService = movementQueryService;
    }

    /**
     * POST  /movements : Create a new movement.
     *
     * @param movement the movement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new movement, or with status 400 (Bad Request) if the movement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/movements")
    @Timed
    public ResponseEntity<Movement> createMovement(@Valid @RequestBody Movement movement) throws URISyntaxException {
        log.debug("REST request to save Movement : {}", movement);
        if (movement.getId() != null) {
            throw new BadRequestAlertException("A new movement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Movement result = movementService.save(movement);
        return ResponseEntity.created(new URI("/api/movements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /movements : Updates an existing movement.
     *
     * @param movement the movement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated movement,
     * or with status 400 (Bad Request) if the movement is not valid,
     * or with status 500 (Internal Server Error) if the movement couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/movements")
    @Timed
    public ResponseEntity<Movement> updateMovement(@Valid @RequestBody Movement movement) throws URISyntaxException {
        log.debug("REST request to update Movement : {}", movement);
        if (movement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Movement result = movementService.save(movement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, movement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /movements : get all the movements.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of movements in body
     */
    @GetMapping("/movements")
    @Timed
    public ResponseEntity<List<Movement>> getAllMovements(MovementCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Movements by criteria: {}", criteria);
        Page<Movement> page = movementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/movements");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /movements/count : count all the movements.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/movements/count")
    @Timed
    public ResponseEntity<Long> countMovements(MovementCriteria criteria) {
        log.debug("REST request to count Movements by criteria: {}", criteria);
        return ResponseEntity.ok().body(movementQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /movements/:id : get the "id" movement.
     *
     * @param id the id of the movement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the movement, or with status 404 (Not Found)
     */
    @GetMapping("/movements/{id}")
    @Timed
    public ResponseEntity<Movement> getMovement(@PathVariable Long id) {
        log.debug("REST request to get Movement : {}", id);
        Optional<Movement> movement = movementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(movement);
    }

    /**
     * DELETE  /movements/:id : delete the "id" movement.
     *
     * @param id the id of the movement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/movements/{id}")
    @Timed
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
        log.debug("REST request to delete Movement : {}", id);
        movementService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/movements?query=:query : search for the movement corresponding
     * to the query.
     *
     * @param query the query of the movement search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/movements")
    @Timed
    public ResponseEntity<List<Movement>> searchMovements(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Movements for query {}", query);
        Page<Movement> page = movementService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/movements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
