package br.com.servicedijkstra.controller;

import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.service.DijkstraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Validated
@RestController
@RequestMapping(value ="/api/v/1.0/dijkstra")
@CrossOrigin(origins = "*")
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
    public void listAllRoutes(@RequestParam(name = "grafoID") String grafoID,
                                          @RequestParam(name = "origem") String origem,
                                          @RequestParam(name = "destino") String destino,
                                          @RequestParam(name = "qtd") int qtd,
                                          HttpServletResponse response) {
        dijkstraService.listRoutes(grafoID,origem,destino, qtd, response) ;
        System.out.println("FIM");
    }

}
