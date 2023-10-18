package br.com.servicedijkstra.dto;

import br.com.servicedijkstra.deserializer.GrafoHashMapDeserializer;
import br.com.servicedijkstra.serializer.GrafoHashMapSerializer;
import br.com.servicedijkstra.swagger.example.SwaggerEndPointExample;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;

@Data
@RedisHash(value = "Grafo")
@AllArgsConstructor
@JsonDeserialize(using = GrafoHashMapDeserializer.class)
@JsonSerialize(using = GrafoHashMapSerializer.class)
@ApiModel(value = "GrafoDTO", description = "Ex.:"+ SwaggerEndPointExample.ADD_GRAFO)
public class GrafoDTO {
    @JsonIgnore
    @Id
    private String idDTO;

    @NonNull
    @JsonUnwrapped
    private HashMap<String,VerticeDTO> verticeMapDto;

}
