package br.com.servicedijkstra.controller;


import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.service.ConfigGrafoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value="/api/v/1.0/dijkstra/grafo")
public class DijkstraConfigController {

    @Autowired
    ConfigGrafoService configGrafoService;


    @GetMapping(value="/addgrafo")
    public ServiceReturnDTO addGrafo(@RequestBody(required = false) @Valid GrafoDTO grafo ) {
            return configGrafoService.addGrafo(grafo)  ;
    }

    @GetMapping(value="/findbyId")
    public GrafoDTO findById(@RequestParam(name = "grafoID") String grafoID) {
        return configGrafoService.findByID(grafoID);
    }
}
