package br.com.servicedijkstra.deserializer;

import br.com.servicedijkstra.dto.ArestaDTO;
import br.com.servicedijkstra.dto.GrafoDTO;
import br.com.servicedijkstra.dto.VerticeDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GrafoHashMapDeserializer extends StdDeserializer<GrafoDTO> {


    public GrafoHashMapDeserializer() {
        super(HashMap.class);
    }
    /**
     * @param p    Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return
     * @throws IOException
     * @throws JacksonException
     */
    @Override
    public GrafoDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode root = p.getCodec().readTree(p);
        var result = new GrafoDTO(UUID.randomUUID().toString(), new HashMap<>());
        for (JsonNode node : root) {
            String id = node.get("id").asText();
            VerticeDTO verticeDTO = new VerticeDTO();
            verticeDTO.setIdVertice(id);
            verticeDTO.setArestaDataDTO(new ArrayList<>());
            node.get("connections").elements().forEachRemaining(vertice -> {
                verticeDTO.getArestaDataDTO().add(new ArestaDTO(vertice.get("id").asText(), vertice.get("cost").asDouble()));
            });
            result.getVerticeMapDto().put(id, verticeDTO);
        }
        return result;
    }
}
