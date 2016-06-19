package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import com.db.Database;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeHelpers.RouteRequest;
import com.routeHelpers.dataTypes.EventData;
import com.routeHelpers.dataTypes.MessageData;
import com.routeHelpers.dataTypes.SpaceData;
import com.audio.PlaySound;

/**
 * Created by a623557 on 23-5-2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Routes implements Runnable {
    Socket clientSocket;
    Database database;
    ObjectMapper mapper = new ObjectMapper();

    /*
        JMX
     */
    public void route(String[] data) throws IOException {
        String json = data[1];
        if (checkRoute(data[0], new String[]{"POST", "bk_java", "insert"})) {
            EventData event = mapper.readValue(json, EventData.class);

            if (database.insertEvent(event))
                new RouteRequest(json, "insert", clientSocket, "Event added.");
            else
                respond("500", "Event could not be added.");

        } else if(checkRoute(data[0], new String[]{"POST", "bk_java", "spaceStatistics"})) {
            SpaceData space = mapper.readValue(json, SpaceData.class);
            Long spaceStats = database.getSpaceStats(space.getSpaceId());

            if (spaceStats != null)
                new RouteRequest(json, "spaceStatistics", clientSocket, spaceStats.toString());
            else
                respond("500", "Could not get or find space statistics.");
        } else if(checkRoute(data[0], new String[]{"POST", "bk_java", "messageStatistics"})) {
            MessageData message = mapper.readValue(json, MessageData.class);
            Long messageStats = database.getMessageStats(message.messageId);

            if (messageStats != null)
                new RouteRequest(json, "messageStatistics", clientSocket, messageStats.toString());
            else
                respond("500", "Could not get or find message statistics.");
        } else {
            respond("404", "Did not find that command.");
            System.err.println("Did not recognize socket: "+data[0] + " - " + json);
        }

        //PlaySound.getInstance("1").play();
        clientSocket.close();
    }

    public Routes(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        this.database = database;
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public Boolean checkRoute(String path, String[] routeElements) {
        for (String element:routeElements) {
            if (!path.contains(element)) return false;
        }
        return true;
    }

    private void respond(String statusCode, String response) throws IOException {
        clientSocket.getOutputStream().write(("HTTP/1.1 "+statusCode+" OK\r\n\r\n" + response).getBytes("UTF-8"));
    }
    /*
        @return ["metaData", "body"]
     */
    public String[] getHttpData() throws IOException {
        InputStreamReader inputStream = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader reader = new BufferedReader(inputStream);
        int contentLength = 0;
        String contentLengthKey = "Content-Length: ";

        //Get metaData
        ArrayList<String> metaData = new ArrayList<String>();
        String metaDataLine;
        while(!(metaDataLine = reader.readLine()).isEmpty()) {
            metaData.add(metaDataLine);

            if (metaDataLine.startsWith(contentLengthKey))
                contentLength = Integer.parseInt(metaDataLine.substring(contentLengthKey.length()));
        }

        //Get body
        StringBuilder body = new StringBuilder();
        if (contentLength != 0) {
            for (int i = 0; i < contentLength; i++) {
                body.append((char) reader.read());
            }
        }

        //reader.close();
        return new String[]{metaData.toString(), body.toString()};
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public void run() {
        try {
            route(getHttpData());
        } catch (IOException e) {
            System.err.println("Error #1: " + e);
        }
    }
}
