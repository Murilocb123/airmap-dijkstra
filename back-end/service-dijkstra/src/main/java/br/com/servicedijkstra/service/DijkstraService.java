package br.com.servicedijkstra.service;

import br.com.servicedijkstra.data.MapFileAndDistance;
import br.com.servicedijkstra.data.ResultOperationData;
import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.enums.AlgsEnums;
import br.com.servicedijkstra.enums.StatusOperationEnum;
import br.com.servicedijkstra.repository.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DijkstraService {
    @Autowired
    private GrafoRepository grafoRepository;
    private GrafoDTO grafoDTO;

    private File file;

    private BufferedWriter bufferedWriter;

    ArrayList<MapFileAndDistance> mapFileAndDistance;
    private int lines=0;
    private int totalCaraAnInt = 0;

    /**
     *  Método que retorna a melhor rota usando Dijkstra
     */
    public ServiceReturnDTO getBestRoute(String grafoID, String origem, String destino) {
        ServiceReturnDTO serviceReturnDTO = new ServiceReturnDTO();
        ResultOperationData resultOperationData = new ResultOperationData(0.0, "");
        try {
            this.doProcess(grafoID, origem, destino, AlgsEnums.DIJKSTRA);
            resultOperationData.setDistance(grafoDTO.getVerticeMapDto().get(destino).getDistance().doubleValue());
            String path = this.getListToString(grafoDTO.getVerticeMapDto().get(destino).getIdsCaminho());
            resultOperationData.setPath(path);
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.SUCCESS.getStatus());
            serviceReturnDTO.setMessage("Operação realizada com sucesso");
            serviceReturnDTO.setData(resultOperationData);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            serviceReturnDTO.setMessage(e.getMessage());
            serviceReturnDTO.setStatusOperation(StatusOperationEnum.NOTFOUND.getStatus());
        }finally {
            this.close();
            this.finalizeOperation();
        }
            return serviceReturnDTO;
    }

    /**
     * Método que lista todas as rotas usando DFS
     * @return
     */
    public void listRoutes(String grafoID, String origem, String destino, int qtd, HttpServletResponse response) {
        try{
            if (!origem.equals(destino)){
                this.doProcess(grafoID, origem, destino, AlgsEnums.All_PATHS);
                this.doSortByListAndLimit(qtd);
                this.readTempFileAndWriteResponse(response ,qtd);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getOutputStream().write("Origem e destino não podem ser iguais".getBytes());
            }

        }catch (NoSuchElementException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally {
            this.close();
            this.finalizeOperation();
        }
    }

    private void doProcess(String grafoID, String origem, String destino, AlgsEnums algsEnums) throws NoSuchElementException{


        grafoRepository.findById(grafoID).ifPresent(grafo -> {grafoDTO = grafo;});

        if (grafoDTO != null) {
            if (!grafoDTO.getVerticeMapDto().containsKey(origem) && !grafoDTO.getVerticeMapDto().containsKey(destino)){
                throw new NoSuchElementException("Origem e destino não encontrados", new NoSuchElementException());
            }
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
        this.mapFileAndDistance = new ArrayList<>();
        //Pego meu vertice de origem
        grafoDTO.getVerticeMapDto().get(origem).setDistance(0.0);

        long time = System.currentTimeMillis();
        try {
            var rota = new ArrayList<String>();
            rota.add(origem);
            this.listAllRoutesUtil(origem, destino, 0.0f,rota);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - time));
    }


    /**
     * Método que lista todas as rotas usando DFS
     *
     */
    private void listAllRoutesUtil(String atual, String destino, double distancia, ArrayList<String> rota ){
        //caso o atual for igual ao destino nao a necessidade de continuar
        if(atual.equals(destino)){
            var rotaString = getListToString(rota);
            writeTempFile(rotaString);
            var resultRota = new MapFileAndDistance(totalCaraAnInt, distancia);
            this.mapFileAndDistance.add(resultRota);
            totalCaraAnInt+=rotaString.length()+1;
            lines+=1;
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
        StringBuilder str = new StringBuilder();
        int size = list.size();
        int count = 1;
        for (var item : list) {
            if (count == size ) {
                str.append(item);
                break;
            }
            str.append(item).append("-");
            count++;
        }
        return str.toString();
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
    private void doSortByListAndLimit(int qtd){
        this.mapFileAndDistance.sort((o1, o2) -> {
            if (o1.getDistance() > o2.getDistance()) {
                return 1;
            } else if (o1.getDistance() < o2.getDistance()) {
                return -1;
            }
            return 0;
        });
        if(qtd <= 0) {
            return;
        }
        System.out.println("Tamanho da lista: " + this.mapFileAndDistance.size());
       // this.mapFileAndDistance.removeIf((item)-> this.mapFileAndDistance.indexOf(item) >= qtd);
    }


    private void writeTempFile(String route){

        try {
            if (this.file == null){
                file = File.createTempFile("routes"+UUID.randomUUID().toString(), ".tmp");
                file.deleteOnExit();
            }
            if(bufferedWriter == null){
                bufferedWriter = new BufferedWriter(new FileWriter(file));
            }
            bufferedWriter.write(route);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readTempFileAndWriteResponse(HttpServletResponse response, int qtd) {
        try {
            response.setContentType("application/json");
            response.getOutputStream().write("{".getBytes());
            response.getOutputStream().write("\"totalRoutes\":\"".getBytes());
            response.getOutputStream().write(String.valueOf(this.lines).getBytes());
            response.getOutputStream().write("\",".getBytes());
            response.getOutputStream().write("\"routes\":[".getBytes());

            response.getOutputStream().flush();
            var randomAccessFile = new RandomAccessFile(file, "r");
            int count = 1;
            for (MapFileAndDistance item : this.mapFileAndDistance) {
                String line = getContentFromTheLine(randomAccessFile, item.getNumberLine());
                response.getOutputStream().write("{\"path\":\"".getBytes());
                response.getOutputStream().write(line.getBytes());
                response.getOutputStream().write("\",\"distance\":".getBytes());
                response.getOutputStream().write(String.valueOf(item.getDistance()).getBytes());
                response.getOutputStream().write("},".getBytes());
                if (count == qtd) {
                    break;
                }
                count++;

                response.getOutputStream().flush();
            }
            response.getOutputStream().write("]".getBytes());
            response.getOutputStream().write("}".getBytes());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            randomAccessFile.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void close(){
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (file != null) {
            file.delete();
        }
    }
    void finalizeOperation(){
        bufferedWriter = null;
        file = null;
        mapFileAndDistance = null;
        grafoDTO = null;
        lines = 0;
        totalCaraAnInt=0;
    }

    String getContentFromTheLine(RandomAccessFile file, int line) throws IOException {
        file.seek(line);
        return file.readLine();
    }
}