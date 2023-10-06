package br.com.servicedijkstra.dto;

import br.com.servicedijkstra.deserializer.GrafoHashMapDeserializer;
import br.com.servicedijkstra.serializer.GrafoHashMapSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;

@Data
@RedisHash("Grafo")
@AllArgsConstructor
@JsonDeserialize(using = GrafoHashMapDeserializer.class)
@JsonSerialize(using = GrafoHashMapSerializer.class)
public class GrafoDTO {
    @Id
    private String idDTO;
    @NonNull
    private HashMap<String,VerticeDTO> verticeMapDto;

}
