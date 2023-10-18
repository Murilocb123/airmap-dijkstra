package br.com.servicedijkstra.controller;

import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.service.DijkstraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequestMapping(value ="/api/v/1.0/dijkstra")
public class DijkstraController {

    @Autowired
    private DijkstraService dijkstraService;

    @GetMapping(value = "/get-best-route")
    public ServiceReturnDTO listRoutes(@RequestParam(name = "grafoID") String grafoID,
                                       @RequestParam(name = "origem") String origem,
                                       @RequestParam(name = "destino") String destino){
        return dijkstraService.getBestRoute(grafoID,origem,destino);
    }

    @GetMapping(value = "/list-all-routes")
    public ServiceReturnDTO listAllRoutes(@RequestParam(name = "grafoID") String grafoID,
                                          @RequestParam(name = "origem") String origem,
                                          @RequestParam(name = "destino") String destino,
                                          @RequestParam(name = "qtd") int qtd){
        return dijkstraService.listRoutes(grafoID,origem,destino, qtd);
    }

}
