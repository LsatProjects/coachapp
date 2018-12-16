package br.com.lsat.coachapp.repository.search;

import br.com.lsat.coachapp.domain.Movement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Movement entity.
 */
public interface MovementSearchRepository extends ElasticsearchRepository<Movement, Long> {
}
