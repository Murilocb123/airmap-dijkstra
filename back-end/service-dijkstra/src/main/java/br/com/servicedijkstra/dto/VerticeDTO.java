package br.com.servicedijkstra.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class VerticeDTO {
    
    @JsonProperty(value = "id")
    private String idVertice;
    @JsonProperty(value = "connections")
    private ArrayList<ArestaDTO> arestaDataDTO;
    @JsonIgnore
    private boolean isVisited = false;
    @JsonIgnore
    private Double distance = Double.MAX_VALUE; // infinito
    @JsonIgnore
    private ArrayList<String> idsCaminho = new ArrayList<>();
    @JsonIgnore
    private String verticePai = "";
}
