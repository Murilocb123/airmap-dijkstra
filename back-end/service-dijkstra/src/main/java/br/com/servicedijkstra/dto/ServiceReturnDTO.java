package br.com.servicedijkstra.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceReturnDTO {
    private String message;
    private int statusOperation;
    private Object additionalData;
    private Object data;
}
