package br.com.servicedijkstra.data;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ResultOperationData {
    double distance;
    ArrayList<String> path;
}
