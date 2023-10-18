package br.com.servicedijkstra.service;

import br.com.servicedijkstra.data.ResultOperationData;
import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.enums.AlgsEnums;
import br.com.servicedijkstra.enums.StatusOperationEnum;
import br.com.servicedijkstra.repository.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Service
public class DijkstraService {
    @Autowired
    private GrafoRepository grafoRepository;
    private GrafoDTO grafoDTO;

    ArrayList<ResultOperationData> listRoutes;
    HashMap<String, String> additionalData;

    /**
     *  Método que retorna a melhor rota usando Dijkstra
     */
    public ServiceReturnDTO getBestRoute(String grafoID, String origem, String destino) {
        ServiceReturnDTO serviceReturnDTO = new ServiceReturnDTO();
        ResultOperationData resultOperationData = new ResultOperationData(0.0, "");
        try {
            this.doProcess(grafoID, origem, destino, AlgsEnums.DIJKSTRA);
            resultOperationData.setDistance(grafoDTO.getVerticeMapDto().get(destino).getDistance());
            String path = this.getListToString(grafoDTO.getVerticeMapDto().get(destino).getIdsCaminho());
            resultOperationData.setPath(path);
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.SUCCESS.getStatus());
            serviceReturnDTO.setMessage("Operação realizada com sucesso");
            serviceReturnDTO.setData(resultOperationData);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.NOTFOUND.getStatus());
        }catch (Exception e) {
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.ERROR.getStatus());
        }
            return serviceReturnDTO;
    }

    /**
     * Método que lista todas as rotas usando DFS
     * @return
     */
    public ServiceReturnDTO listRoutes(String grafoID, String origem, String destino, int qtd) {
        ServiceReturnDTO serviceReturnDTO = new ServiceReturnDTO();
        try{
            this.doProcess(grafoID, origem, destino, AlgsEnums.All_PATHS);
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.SUCCESS.getStatus());
            serviceReturnDTO.setMessage("Operação realizada com sucesso");
            serviceReturnDTO.setData(this.getRoutesForQTD(qtd));
            serviceReturnDTO.setAdditionalData(this.additionalData);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.NOTFOUND.getStatus());
        }catch (Exception e) {
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.ERROR.getStatus());
        }
        return serviceReturnDTO;
    }

    private void doProcess(String grafoID, String origem, String destino, AlgsEnums algsEnums) throws NoSuchElementException{
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
        }else{
           throw new NoSuchElementException("Grafo não encontrado", new NoSuchElementException());
        }
    }

    private void dijkstra(String origem, String destino){
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
//                        System.out.println("Vertice já visitado");
                        return;
                    }

                    var cost = (currentVertice.getDistance() + arestaDTO.getCostDTO());
                    if (conexaoVerticeDTO.getDistance() > cost) {
//                        if (conexaoVerticeDTO.getIdVertice().equals(destino)){
//                            System.out.println("Destino encontrado");
//                        }
//                        if (conexaoVerticeDTO.getIdVertice().equals(destino)){
//                            System.out.println("Destino encontrado");
//                        }
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

        long time = System.currentTimeMillis();
        if(origem.equals(destino)){
            ResultOperationData result = new ResultOperationData(0.0, origem);
            this.listRoutes.add(result);
            return;
        }
        try {
            var rota = new ArrayList<String>();
            rota.add(origem);
            //Insiro o vertice de origem
            this.listAllRoutesUtil(origem, destino, 0.0,rota);
            int totalTime = (int) (System.currentTimeMillis() - time);
            this.additionalData = new HashMap<>();
            this.additionalData.put("totalTime", String.valueOf(totalTime));
            this.additionalData.put("totalRoutes", String.valueOf(this.listRoutes.size()));
           // fWrite.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - time));
    }


    /**
     * Método que lista todas as rotas usando DFS
     *
     */
    private void listAllRoutesUtil(String atual, String destino, double distancia, ArrayList<String> rota){
        //caso o atual for igual ao destino nao a necessidade de continuar
        if(atual.equals(destino)){
            var resultRota = new ResultOperationData(distancia, this.getListToString(rota));
            this.listRoutes.add(resultRota);
            return;
        }
        grafoDTO.getVerticeMapDto().get(atual).setVisited(true);
        //percorro e vou empilhando todas as minhas conexões
        var listArestas = grafoDTO.getVerticeMapDto().get(atual).getArestaDataDTO();
        for (var aresta : listArestas) {
            if (!grafoDTO.getVerticeMapDto().get(aresta.getIdArestaDTO()).isVisited()) {
                distancia += aresta.getCostDTO();
                rota.add(aresta.getIdArestaDTO());
                listAllRoutesUtil(aresta.getIdArestaDTO(), destino, distancia,rota);
                rota.remove(aresta.getIdArestaDTO());
                distancia -= aresta.getCostDTO();
            }
        }
        grafoDTO.getVerticeMapDto().get(atual).setVisited(false);
    }




    private String getListToString(ArrayList<String> list){
        String str = "";
        int size = list.size();
        int count = 1;
        for (var item : list) {
            if (count == size ) {
                str += item;
                break;
            }
            str += item + "-";
            count++;
        }
        return str;
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
    private ArrayList<ResultOperationData> getRoutesForQTD(int qtd){
        this.listRoutes.sort((o1, o2) -> {
            if (o1.getDistance() > o2.getDistance()) {
                return 1;
            } else if (o1.getDistance() < o2.getDistance()) {
                return -1;
            }
            return 0;
        });
        if(qtd <= 0) {
            return this.listRoutes;
        }
        var list = new ArrayList<ResultOperationData>();
        int count = 1;
        for (var item : this.listRoutes) {
            if (count > qtd ) {
                break;
            }
            list.add(item);
            count++;
        }
        return list;

    }
}