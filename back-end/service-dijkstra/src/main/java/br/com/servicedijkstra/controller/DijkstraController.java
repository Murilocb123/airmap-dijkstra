package br.com.servicedijkstra.controller;

import br.com.servicedijkstra.service.DijkstraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@Validated
@RestController
@RequestMapping(value ="/api/v/1.0/dijkstra")
public class DijkstraController {

    @Autowired
    private DijkstraService dijkstraService;

    @GetMapping(value = "/listroutes")
    public ArrayList<String> listRoutes(@RequestParam(name = "grafoID") @Valid @NotBlank(message = "O grafoID não pode ser vazio") String grafoID){
        return dijkstraService.getBestRoute(grafoID,"SC1","RR1");
    }

}
