package br.com.servicedijkstra.repository;

import br.com.servicedijkstra.dto.GrafoDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrafoRepository extends CrudRepository<GrafoDTO,String> {
}
