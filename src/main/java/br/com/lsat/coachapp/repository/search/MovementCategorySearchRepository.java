package br.com.lsat.coachapp.repository.search;

import br.com.lsat.coachapp.domain.MovementCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MovementCategory entity.
 */
public interface MovementCategorySearchRepository extends ElasticsearchRepository<MovementCategory, Long> {
}
