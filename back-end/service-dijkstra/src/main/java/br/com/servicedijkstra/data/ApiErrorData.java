package br.com.servicedijkstra.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorData {
    private Integer status;
    private String message;
    private String details;

}
