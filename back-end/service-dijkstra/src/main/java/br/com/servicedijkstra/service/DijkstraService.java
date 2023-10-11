package br.com.servicedijkstra.service;

import br.com.servicedijkstra.data.ResultOperationData;
import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.enums.AlgsEnums;
import br.com.servicedijkstra.repository.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

@Service
public class DijkstraService {
    @Autowired
    private GrafoRepository grafoRepository;
    private GrafoDTO grafoDTO;

    ArrayList<ResultOperationData> listRoutes;

    public ServiceReturnDTO getBestRoute(String grafoID, String origem, String destino) {
        ServiceReturnDTO serviceReturnDTO = new ServiceReturnDTO();
        ResultOperationData resultOperationData = new ResultOperationData();
        try {
            this.doProcess(grafoID, origem, destino, AlgsEnums.DIJKSTRA);
            resultOperationData.setDistance(grafoDTO.getVerticeMapDto().get(destino).getDistance());
            resultOperationData.setPath(grafoDTO.getVerticeMapDto().get(destino).getIdsCaminho());
            serviceReturnDTO.setStatusOperation("SUCCESS");
            serviceReturnDTO.setMessage("Operação realizada com sucesso");
            serviceReturnDTO.setData(resultOperationData);
        }catch (Exception e) {
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation("ERROR");
        }
            return serviceReturnDTO;
    }
    public ServiceReturnDTO listRoutes(String grafoID, String origem, String destino) {
        ServiceReturnDTO serviceReturnDTO = new ServiceReturnDTO();
        try{
            this.doProcess(grafoID, origem, destino, AlgsEnums.All_PATHS);
            serviceReturnDTO.setStatusOperation("SUCCESS");
            serviceReturnDTO.setMessage("Operação realizada com sucesso");
            serviceReturnDTO.setData(this.listRoutes);
        }catch (Exception e) {
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation("ERROR");
        }
        try {

            System.out.println(this.listRoutes.size());

        }catch (Exception e){
            e.printStackTrace();
        }
        return serviceReturnDTO;
    }

    private void doProcess(String grafoID, String origem, String destino, AlgsEnums algsEnums) throws Exception{
        grafoRepository.findById(grafoID).ifPresent(grafo -> {grafoDTO = grafo;});

        if (grafoDTO != null) {
            switch (algsEnums) {
                case DIJKSTRA:
                    this.dijkstra(origem, destino);
                    break;
                case All_PATHS:
                    this.listAllRoutes(origem, destino);
                    break;
            }
        }
    }

    private void dijkstra(String origem, String destino) throws Exception{
        //Pego meu vertice de origem
        grafoDTO.getVerticeMapDto().get(origem).setDistance(0.0);
        //Insiro a menor distacia
        ArrayList<String> verticesNaoVisitadosId = this.getListNotVisited();
        while (!verticesNaoVisitadosId.isEmpty()){
            for (String arestaVerticeID : verticesNaoVisitadosId) {
                //Vertice atual
                var currentVertice = grafoDTO.getVerticeMapDto().get(arestaVerticeID);

                //Percorrendo suas conexões e verificando se a distancia é menor
                currentVertice.getArestaDataDTO().forEach(arestaDTO -> {
                    var conexaoVerticeDTO = grafoDTO.getVerticeMapDto().get(arestaDTO.getIdArestaDTO());
                    if (conexaoVerticeDTO.getVerticePai().equals(arestaVerticeID)) {
                        System.out.println("Vertice já visitado");
                        return;
                    }
                    var cost = (currentVertice.getDistance() + arestaDTO.getCostDTO());
                    if (conexaoVerticeDTO.getDistance() > cost) {
                        conexaoVerticeDTO.setDistance(cost);
                        conexaoVerticeDTO.setVerticePai(arestaVerticeID);
                        conexaoVerticeDTO.setIdsCaminho(new ArrayList<>(currentVertice.getIdsCaminho()));
                        conexaoVerticeDTO.getIdsCaminho().add(arestaVerticeID);
                    }
                });
                currentVertice.setVisited(true);
                currentVertice.getIdsCaminho().add(arestaVerticeID);
                verticesNaoVisitadosId = this.getListNotVisited();
            }
        }
        grafoDTO.getVerticeMapDto().get(destino).setIdsCaminho(this.getListCaminho(destino));
    }

    /**
     * Método que lista todas as rotas usando DFS
     * @param origem
     * @param destino
     * @return
     */
    private void listAllRoutes(String origem, String destino) {
        //Inicia lista de rotas
        this.listRoutes = new ArrayList<ResultOperationData>();
        //Pego meu vertice de origem
        grafoDTO.getVerticeMapDto().get(origem).setDistance(0.0);
        ResultOperationData rota = new ResultOperationData();
        rota.setPath(new ArrayList<>());
        if(origem.equals(destino)){
            rota.setDistance(0.0);
            rota.getPath().add(origem);
            this.listRoutes.add(rota);
            return;
        }
        try {
            var file = new File("D:\\muril\\Downloads\\teste.txt");
            System.out.println(file.getAbsolutePath());
            //var fWrite = new FileWriter(file);
            FileWriter fWrite = null;
            //Insiro o vertice de origem
            this.listAllRoutesUtil(origem, destino, rota, fWrite);
           // fWrite.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Método que lista todas as rotas usando DFS
     *
     */
    private void listAllRoutesUtil(String atual, String destino, ResultOperationData rota, FileWriter fWrite){
        //caso o atual for igual ao destino nao a necessidade de continuar
        if(atual.equals(destino)){
            this.listRoutes.add(rota);
            rota = new ResultOperationData();
            rota.setPath(new ArrayList<>());
            return;
        }
        grafoDTO.getVerticeMapDto().get(atual).setVisited(true);
        //percorro e vou empilhando todas as minhas conexões
        var listArestas = grafoDTO.getVerticeMapDto().get(atual).getArestaDataDTO();
        for (var aresta : listArestas) {
            if (!grafoDTO.getVerticeMapDto().get(aresta.getIdArestaDTO()).isVisited()) {
                rota.setDistance(rota.getDistance() + aresta.getCostDTO());
                rota.getPath().add(aresta.getIdArestaDTO());
                listAllRoutesUtil(aresta.getIdArestaDTO(), destino, rota, fWrite);
            }
        }
        grafoDTO.getVerticeMapDto().get(atual).setVisited(false);
    }




    private  ArrayList<String>  getListCaminho(String destino) {
        var verticePai =grafoDTO.getVerticeMapDto().get(destino).getVerticePai();
        var list = new ArrayList<String>();
        list.add(destino);
        while (!verticePai.isEmpty()) {
            list.add(verticePai);
            verticePai = grafoDTO.getVerticeMapDto().get(verticePai).getVerticePai();
        }
        return list;
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