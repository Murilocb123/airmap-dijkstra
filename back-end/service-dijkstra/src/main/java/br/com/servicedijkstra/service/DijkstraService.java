package br.com.servicedijkstra.service;

import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.VerticeDTO;
import br.com.servicedijkstra.enums.AlgsEnums;
import br.com.servicedijkstra.repository.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class DijkstraService {
    @Autowired
    private GrafoRepository grafoRepository;
    private GrafoDTO grafoDTO;

    public ArrayList<String> getBestRoute(String grafoID, String origem, String destino) {
        return this.doProcess(grafoID, origem, destino, AlgsEnums.DIJKSTRA);
    }
    public ArrayList<String> listRoutes(String grafoID, String origem, String destino) {
        return this.doProcess(grafoID, origem, destino, AlgsEnums.All_PATHS);
    }

    private ArrayList<String> doProcess(String grafoID, String origem, String destino, AlgsEnums algsEnums) {
        grafoRepository.findById(grafoID).ifPresent(grafo -> {grafoDTO = grafo;});

        if (grafoDTO != null) {
            //grafo.get("SC1").getArestasData().get(0).getVerticeData().setDistance(2000.00);
            //System.out.println(grafo.get("SC1").getArestasData().get(0).getVerticeData().getDistance());
            //System.out.println(grafo.get("RS1").getDistance());
            if(algsEnums.equals(AlgsEnums.DIJKSTRA)) {
                this.dijkstra(origem);
            }
        }
        return grafoDTO.getVerticeMapDto().get(destino).getIdsCaminho();
    }

    private void dijkstra(String origem){
        //Pego meu vertice de origem
        grafoDTO.getVerticeMapDto().get(origem).setDistance(0.0);
        //Insiro a menor distacia
        ArrayList<String> verticesNaoVisitadosId = this.getListNotVisited();
        while (!isVerifyVisitedAll()){
            for (String arestaVerticeID : verticesNaoVisitadosId) {
                var currentVertice = grafoDTO.getVerticeMapDto().get(arestaVerticeID);
                currentVertice.getArestaDataDTO().forEach(arestaDTO -> {
                    var conexaoVerticeDTO = grafoDTO.getVerticeMapDto().get(arestaDTO.getIdArestaDTO());
                    if (conexaoVerticeDTO.isVisited()) {
                        System.out.println("Vertice já visitado");
                        return;
                    }
                    var cost = arestaDTO.getCostDTO();
                    if (conexaoVerticeDTO.getDistance() > currentVertice.getDistance() + cost) {
                        conexaoVerticeDTO.setDistance(currentVertice.getDistance() + cost);
                        conexaoVerticeDTO.setVerticePai(arestaVerticeID);
                        conexaoVerticeDTO.setIdsCaminho(currentVertice.getIdsCaminho());
                        conexaoVerticeDTO.getIdsCaminho().add(arestaVerticeID);
                    }
                });
                currentVertice.setVisited(true);
            }
            verticesNaoVisitadosId.clear();
            verticesNaoVisitadosId = this.getListNotVisited();


        }
    }



    private boolean isVerifyVisitedAll(){
        boolean result = true;
        for (Map.Entry<String, VerticeDTO> entry : grafoDTO.getVerticeMapDto().entrySet()) {
            VerticeDTO value = entry.getValue();
            if (!value.isVisited()) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getListNotVisited() {
        var list = new ArrayList<String>();
        grafoDTO.getVerticeMapDto().forEach((key, value) -> {
            if (!value.isVisited() && value.getDistance() != Double.MAX_VALUE) {
                list.add(key);
            }
        });
        return list;
    }
}