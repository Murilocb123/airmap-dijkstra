package br.com.servicedijkstra.serializer;

import br.com.servicedijkstra.dto.GrafoDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GrafoHashMapSerializer extends StdSerializer<GrafoDTO>{
    public GrafoHashMapSerializer() {
        super(GrafoDTO.class);
    }

    /**
     * @param value    Value to serialize; can <b>not</b> be null.
     * @param jsonOut   Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     *                 serializing Objects value contains, if any.
     * @throws IOException
     */
    @Override
    public void serialize(GrafoDTO value, JsonGenerator jsonOut, SerializerProvider provider) throws IOException {
        jsonOut.writeStartArray();
        value.getVerticeMapDto().forEach((id, vertice) -> {
            try {
                jsonOut.writeStartObject();
                jsonOut.writeStringField("id", id);
                jsonOut.writeArrayFieldStart("connections");
                vertice.getArestaDataDTO().forEach(aresta -> {
                    try {
                        jsonOut.writeStartObject();
                        jsonOut.writeStringField("id", aresta.getIdArestaDTO());
                        jsonOut.writeNumberField("cost", aresta.getCostDTO());
                        jsonOut.writeEndObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                jsonOut.writeEndArray();
                jsonOut.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
