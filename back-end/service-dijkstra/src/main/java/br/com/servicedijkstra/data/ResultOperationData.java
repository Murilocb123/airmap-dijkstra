package br.com.servicedijkstra.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
public class ResultOperationData {
    double distance;
    String path;
}
