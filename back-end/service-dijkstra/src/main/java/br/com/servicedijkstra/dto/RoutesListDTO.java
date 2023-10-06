package br.com.servicedijkstra.dto;


import lombok.Data;

import java.util.ArrayList;

@Data
public class RoutesListDTO {
    private String id;
    private ArrayList<String[]> routes;
    private String description;

}
