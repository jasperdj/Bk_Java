package com.testing;

/**
 * Created by a623557 on 26-5-2016.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeHelpers.dataTypes.EventData;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoutesSerialization {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static void main(String[] arg) throws IOException {
        String json = "{\"spaceId\":5, \"messageId\": 5, \"eventType\": 5, \"nodeId\":1, \"forceException\": false}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        EventData event = mapper.readValue(json, EventData.class);
    }
}
