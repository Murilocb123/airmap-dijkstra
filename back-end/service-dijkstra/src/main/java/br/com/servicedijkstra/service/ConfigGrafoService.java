package br.com.servicedijkstra.service;


import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.ServiceReturnDTO;
import br.com.servicedijkstra.enums.StatusOperationEnum;
import br.com.servicedijkstra.repository.GrafoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ConfigGrafoService {

    @Autowired
    GrafoRepository grafoRepository;

    public ServiceReturnDTO addGrafo(GrafoDTO grafoDTO) {
        grafoRepository.save(grafoDTO);
        var grafoInsertInfo = new HashMap<String,String>();
        grafoInsertInfo.put("grafoID",grafoDTO.getIdDTO());
        return new ServiceReturnDTO("Inserido com sucesso", StatusOperationEnum.SUCCESS.getStatus(), null, grafoInsertInfo);

    }
    public GrafoDTO findByID(String grafoID) {
        if (grafoRepository.findById(grafoID).isEmpty()) {
            throw new RuntimeException("Grafo não encontrado");
        }
        return grafoRepository.findById(grafoID).get();

    }


}
