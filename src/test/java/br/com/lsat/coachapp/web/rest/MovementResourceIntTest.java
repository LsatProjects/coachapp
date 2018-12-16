package br.com.lsat.coachapp.web.rest;

import br.com.lsat.coachapp.CoachappApp;

import br.com.lsat.coachapp.domain.Movement;
import br.com.lsat.coachapp.domain.MovementCategory;
import br.com.lsat.coachapp.repository.MovementRepository;
import br.com.lsat.coachapp.repository.search.MovementSearchRepository;
import br.com.lsat.coachapp.service.MovementService;
import br.com.lsat.coachapp.web.rest.errors.ExceptionTranslator;
import br.com.lsat.coachapp.service.dto.MovementCriteria;
import br.com.lsat.coachapp.service.MovementQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static br.com.lsat.coachapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MovementResource REST controller.
 *
 * @see MovementResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoachappApp.class)
public class MovementResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ABBREVIATION = "AAAAAAAAAA";
    private static final String UPDATED_ABBREVIATION = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private MovementService movementService;

    /**
     * This repository is mocked in the br.com.lsat.coachapp.repository.search test package.
     *
     * @see br.com.lsat.coachapp.repository.search.MovementSearchRepositoryMockConfiguration
     */
    @Autowired
    private MovementSearchRepository mockMovementSearchRepository;

    @Autowired
    private MovementQueryService movementQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMovementMockMvc;

    private Movement movement;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MovementResource movementResource = new MovementResource(movementService, movementQueryService);
        this.restMovementMockMvc = MockMvcBuilders.standaloneSetup(movementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movement createEntity(EntityManager em) {
        Movement movement = new Movement()
            .name(DEFAULT_NAME)
            .abbreviation(DEFAULT_ABBREVIATION)
            .note(DEFAULT_NOTE);
        // Add required entity
        MovementCategory movementCategory = MovementCategoryResourceIntTest.createEntity(em);
        em.persist(movementCategory);
        em.flush();
        movement.setMovementCategory(movementCategory);
        return movement;
    }

    @Before
    public void initTest() {
        movement = createEntity(em);
    }

    @Test
    @Transactional
    public void createMovement() throws Exception {
        int databaseSizeBeforeCreate = movementRepository.findAll().size();

        // Create the Movement
        restMovementMockMvc.perform(post("/api/movements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movement)))
            .andExpect(status().isCreated());

        // Validate the Movement in the database
        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeCreate + 1);
        Movement testMovement = movementList.get(movementList.size() - 1);
        assertThat(testMovement.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovement.getAbbreviation()).isEqualTo(DEFAULT_ABBREVIATION);
        assertThat(testMovement.getNote()).isEqualTo(DEFAULT_NOTE);

        // Validate the Movement in Elasticsearch
        verify(mockMovementSearchRepository, times(1)).save(testMovement);
    }

    @Test
    @Transactional
    public void createMovementWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = movementRepository.findAll().size();

        // Create the Movement with an existing ID
        movement.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovementMockMvc.perform(post("/api/movements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movement)))
            .andExpect(status().isBadRequest());

        // Validate the Movement in the database
        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeCreate);

        // Validate the Movement in Elasticsearch
        verify(mockMovementSearchRepository, times(0)).save(movement);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = movementRepository.findAll().size();
        // set the field null
        movement.setName(null);

        // Create the Movement, which fails.

        restMovementMockMvc.perform(post("/api/movements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movement)))
            .andExpect(status().isBadRequest());

        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMovements() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList
        restMovementMockMvc.perform(get("/api/movements?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].abbreviation").value(hasItem(DEFAULT_ABBREVIATION.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())));
    }
    
    @Test
    @Transactional
    public void getMovement() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get the movement
        restMovementMockMvc.perform(get("/api/movements/{id}", movement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(movement.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.abbreviation").value(DEFAULT_ABBREVIATION.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()));
    }

    @Test
    @Transactional
    public void getAllMovementsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where name equals to DEFAULT_NAME
        defaultMovementShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the movementList where name equals to UPDATED_NAME
        defaultMovementShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMovementsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMovementShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the movementList where name equals to UPDATED_NAME
        defaultMovementShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMovementsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where name is not null
        defaultMovementShouldBeFound("name.specified=true");

        // Get all the movementList where name is null
        defaultMovementShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllMovementsByAbbreviationIsEqualToSomething() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where abbreviation equals to DEFAULT_ABBREVIATION
        defaultMovementShouldBeFound("abbreviation.equals=" + DEFAULT_ABBREVIATION);

        // Get all the movementList where abbreviation equals to UPDATED_ABBREVIATION
        defaultMovementShouldNotBeFound("abbreviation.equals=" + UPDATED_ABBREVIATION);
    }

    @Test
    @Transactional
    public void getAllMovementsByAbbreviationIsInShouldWork() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where abbreviation in DEFAULT_ABBREVIATION or UPDATED_ABBREVIATION
        defaultMovementShouldBeFound("abbreviation.in=" + DEFAULT_ABBREVIATION + "," + UPDATED_ABBREVIATION);

        // Get all the movementList where abbreviation equals to UPDATED_ABBREVIATION
        defaultMovementShouldNotBeFound("abbreviation.in=" + UPDATED_ABBREVIATION);
    }

    @Test
    @Transactional
    public void getAllMovementsByAbbreviationIsNullOrNotNull() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where abbreviation is not null
        defaultMovementShouldBeFound("abbreviation.specified=true");

        // Get all the movementList where abbreviation is null
        defaultMovementShouldNotBeFound("abbreviation.specified=false");
    }

    @Test
    @Transactional
    public void getAllMovementsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where note equals to DEFAULT_NOTE
        defaultMovementShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the movementList where note equals to UPDATED_NOTE
        defaultMovementShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void getAllMovementsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultMovementShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the movementList where note equals to UPDATED_NOTE
        defaultMovementShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void getAllMovementsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        movementRepository.saveAndFlush(movement);

        // Get all the movementList where note is not null
        defaultMovementShouldBeFound("note.specified=true");

        // Get all the movementList where note is null
        defaultMovementShouldNotBeFound("note.specified=false");
    }

    //@Test
    @Transactional
    public void getAllMovementsByMovementCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        MovementCategory movementCategory = MovementCategoryResourceIntTest.createEntity(em);
        em.persist(movementCategory);
        em.flush();
        movement.setMovementCategory(movementCategory);
        movementRepository.saveAndFlush(movement);
        Long movementCategoryId = movementCategory.getId();

        // Get all the movementList where movementCategory equals to movementCategoryId
        defaultMovementShouldBeFound("movementCategoryId.equals=" + movementCategoryId);

        // Get all the movementList where movementCategory equals to movementCategoryId + 1
        defaultMovementShouldNotBeFound("movementCategoryId.equals=" + (movementCategoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMovementShouldBeFound(String filter) throws Exception {
        restMovementMockMvc.perform(get("/api/movements?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].abbreviation").value(hasItem(DEFAULT_ABBREVIATION.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())));

        // Check, that the count call also returns 1
        restMovementMockMvc.perform(get("/api/movements/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMovementShouldNotBeFound(String filter) throws Exception {
        restMovementMockMvc.perform(get("/api/movements?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMovementMockMvc.perform(get("/api/movements/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingMovement() throws Exception {
        // Get the movement
        restMovementMockMvc.perform(get("/api/movements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovement() throws Exception {
        // Initialize the database
        movementService.save(movement);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMovementSearchRepository);

        int databaseSizeBeforeUpdate = movementRepository.findAll().size();

        // Update the movement
        Movement updatedMovement = movementRepository.findById(movement.getId()).get();
        // Disconnect from session so that the updates on updatedMovement are not directly saved in db
        em.detach(updatedMovement);
        updatedMovement
            .name(UPDATED_NAME)
            .abbreviation(UPDATED_ABBREVIATION)
            .note(UPDATED_NOTE);

        restMovementMockMvc.perform(put("/api/movements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMovement)))
            .andExpect(status().isOk());

        // Validate the Movement in the database
        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeUpdate);
        Movement testMovement = movementList.get(movementList.size() - 1);
        assertThat(testMovement.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMovement.getAbbreviation()).isEqualTo(UPDATED_ABBREVIATION);
        assertThat(testMovement.getNote()).isEqualTo(UPDATED_NOTE);

        // Validate the Movement in Elasticsearch
        verify(mockMovementSearchRepository, times(1)).save(testMovement);
    }

    @Test
    @Transactional
    public void updateNonExistingMovement() throws Exception {
        int databaseSizeBeforeUpdate = movementRepository.findAll().size();

        // Create the Movement

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMovementMockMvc.perform(put("/api/movements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movement)))
            .andExpect(status().isBadRequest());

        // Validate the Movement in the database
        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Movement in Elasticsearch
        verify(mockMovementSearchRepository, times(0)).save(movement);
    }

    @Test
    @Transactional
    public void deleteMovement() throws Exception {
        // Initialize the database
        movementService.save(movement);

        int databaseSizeBeforeDelete = movementRepository.findAll().size();

        // Get the movement
        restMovementMockMvc.perform(delete("/api/movements/{id}", movement.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Movement> movementList = movementRepository.findAll();
        assertThat(movementList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Movement in Elasticsearch
        verify(mockMovementSearchRepository, times(1)).deleteById(movement.getId());
    }

    @Test
    @Transactional
    public void searchMovement() throws Exception {
        // Initialize the database
        movementService.save(movement);
        when(mockMovementSearchRepository.search(queryStringQuery("id:" + movement.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(movement), PageRequest.of(0, 1), 1));
        // Search the movement
        restMovementMockMvc.perform(get("/api/_search/movements?query=id:" + movement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].abbreviation").value(hasItem(DEFAULT_ABBREVIATION)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Movement.class);
        Movement movement1 = new Movement();
        movement1.setId(1L);
        Movement movement2 = new Movement();
        movement2.setId(movement1.getId());
        assertThat(movement1).isEqualTo(movement2);
        movement2.setId(2L);
        assertThat(movement1).isNotEqualTo(movement2);
        movement1.setId(null);
        assertThat(movement1).isNotEqualTo(movement2);
    }
}
