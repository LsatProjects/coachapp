package br.com.lsat.coachapp.web.rest;

import br.com.lsat.coachapp.CoachappApp;

import br.com.lsat.coachapp.domain.Sport;
import br.com.lsat.coachapp.repository.SportRepository;
import br.com.lsat.coachapp.repository.search.SportSearchRepository;
import br.com.lsat.coachapp.service.SportService;
import br.com.lsat.coachapp.web.rest.errors.ExceptionTranslator;
import br.com.lsat.coachapp.service.dto.SportCriteria;
import br.com.lsat.coachapp.service.SportQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Test class for the SportResource REST controller.
 *
 * @see SportResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoachappApp.class)
public class SportResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private SportService sportService;

    /**
     * This repository is mocked in the br.com.lsat.coachapp.repository.search test package.
     *
     * @see br.com.lsat.coachapp.repository.search.SportSearchRepositoryMockConfiguration
     */
    @Autowired
    private SportSearchRepository mockSportSearchRepository;

    @Autowired
    private SportQueryService sportQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSportMockMvc;

    private Sport sport;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SportResource sportResource = new SportResource(sportService, sportQueryService);
        this.restSportMockMvc = MockMvcBuilders.standaloneSetup(sportResource)
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
    public static Sport createEntity(EntityManager em) {
        Sport sport = new Sport()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return sport;
    }

    @Before
    public void initTest() {
        sport = createEntity(em);
    }

    @Test
    @Transactional
    public void createSport() throws Exception {
        int databaseSizeBeforeCreate = sportRepository.findAll().size();

        // Create the Sport
        restSportMockMvc.perform(post("/api/sports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isCreated());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate + 1);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSport.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Sport in Elasticsearch
        verify(mockSportSearchRepository, times(1)).save(testSport);
    }

    @Test
    @Transactional
    public void createSportWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sportRepository.findAll().size();

        // Create the Sport with an existing ID
        sport.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSportMockMvc.perform(post("/api/sports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sport in Elasticsearch
        verify(mockSportSearchRepository, times(0)).save(sport);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sportRepository.findAll().size();
        // set the field null
        sport.setName(null);

        // Create the Sport, which fails.

        restSportMockMvc.perform(post("/api/sports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isBadRequest());

        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSports() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList
        restSportMockMvc.perform(get("/api/sports?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sport.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get the sport
        restSportMockMvc.perform(get("/api/sports/{id}", sport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sport.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllSportsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where name equals to DEFAULT_NAME
        defaultSportShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sportList where name equals to UPDATED_NAME
        defaultSportShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSportsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSportShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sportList where name equals to UPDATED_NAME
        defaultSportShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSportsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where name is not null
        defaultSportShouldBeFound("name.specified=true");

        // Get all the sportList where name is null
        defaultSportShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSportsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where description equals to DEFAULT_DESCRIPTION
        defaultSportShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the sportList where description equals to UPDATED_DESCRIPTION
        defaultSportShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSportsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSportShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the sportList where description equals to UPDATED_DESCRIPTION
        defaultSportShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSportsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList where description is not null
        defaultSportShouldBeFound("description.specified=true");

        // Get all the sportList where description is null
        defaultSportShouldNotBeFound("description.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSportShouldBeFound(String filter) throws Exception {
        restSportMockMvc.perform(get("/api/sports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sport.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restSportMockMvc.perform(get("/api/sports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSportShouldNotBeFound(String filter) throws Exception {
        restSportMockMvc.perform(get("/api/sports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSportMockMvc.perform(get("/api/sports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSport() throws Exception {
        // Get the sport
        restSportMockMvc.perform(get("/api/sports/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSport() throws Exception {
        // Initialize the database
        sportService.save(sport);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSportSearchRepository);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport
        Sport updatedSport = sportRepository.findById(sport.getId()).get();
        // Disconnect from session so that the updates on updatedSport are not directly saved in db
        em.detach(updatedSport);
        updatedSport
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restSportMockMvc.perform(put("/api/sports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSport)))
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSport.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Sport in Elasticsearch
        verify(mockSportSearchRepository, times(1)).save(testSport);
    }

    @Test
    @Transactional
    public void updateNonExistingSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Create the Sport

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSportMockMvc.perform(put("/api/sports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sport in Elasticsearch
        verify(mockSportSearchRepository, times(0)).save(sport);
    }

    @Test
    @Transactional
    public void deleteSport() throws Exception {
        // Initialize the database
        sportService.save(sport);

        int databaseSizeBeforeDelete = sportRepository.findAll().size();

        // Get the sport
        restSportMockMvc.perform(delete("/api/sports/{id}", sport.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sport in Elasticsearch
        verify(mockSportSearchRepository, times(1)).deleteById(sport.getId());
    }

    @Test
    @Transactional
    public void searchSport() throws Exception {
        // Initialize the database
        sportService.save(sport);
        when(mockSportSearchRepository.search(queryStringQuery("id:" + sport.getId())))
            .thenReturn(Collections.singletonList(sport));
        // Search the sport
        restSportMockMvc.perform(get("/api/_search/sports?query=id:" + sport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sport.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sport.class);
        Sport sport1 = new Sport();
        sport1.setId(1L);
        Sport sport2 = new Sport();
        sport2.setId(sport1.getId());
        assertThat(sport1).isEqualTo(sport2);
        sport2.setId(2L);
        assertThat(sport1).isNotEqualTo(sport2);
        sport1.setId(null);
        assertThat(sport1).isNotEqualTo(sport2);
    }
}
