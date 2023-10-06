package br.com.servicedijkstra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ArestaDTO {
    @JsonProperty(value = "id")
    private String idArestaDTO;
    @DecimalMin(value = "0.0", inclusive = false, message = "O custo da aresta deve ser maior que 0")
    @JsonProperty("cost")
    private Double costDTO;
}
