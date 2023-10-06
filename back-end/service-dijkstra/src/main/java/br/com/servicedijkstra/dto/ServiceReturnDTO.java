package br.com.servicedijkstra.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceReturnDTO {
    private String message;
    private String statusOperation;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
}
