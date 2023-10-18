package br.com.servicedijkstra.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ResultOperationData {
    double distance;
    String path = "";
}
