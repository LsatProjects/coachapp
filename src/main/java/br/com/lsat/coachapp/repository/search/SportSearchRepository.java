package br.com.lsat.coachapp.repository.search;

import br.com.lsat.coachapp.domain.Sport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Sport entity.
 */
public interface SportSearchRepository extends ElasticsearchRepository<Sport, Long> {
}
