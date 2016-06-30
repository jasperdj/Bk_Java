package com.testing;

import com.service.Routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by a623557 on 9-6-2016.
 */
public class httpPerformanceTest {
    Random random = new Random();
    int iteration = 100000;

    public static void main(String[] args) {
        try {
            new httpPerformanceTest();
        } catch (Exception e ) {
            System.out.println("Error: " + e);
        }
    }

    public httpPerformanceTest() throws Exception {
        ServerSocket server = new ServerSocket(9000, 50000);
        while (true) {
            final Socket clientSocket = server.accept();
            new Thread(new Runnable(){
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

                public void route(String[] data) throws Exception {
                    String json = data[1];
                    if (checkRoute(data[0], new String[]{"POST", "NOTHING"})) {
                        try {
                            if (json.toLowerCase().contains("true")) throw new Exception("Forced exception");
                            respond("200", "Succes");
                        } catch (Exception e) {
                            respond("500", "Internal server error " + e);
                        }
                    } else if (checkRoute(data[0], new String[]{"GET", "CPU"})) {
                        for (int i = 0; i < iteration*2; i++) {
                            Math.sqrt(random.nextDouble() * i);
                            Math.abs(random.nextDouble() / i);
                            Math.tan(random.nextDouble() - 1);
                        }
                        respond("200", "Success");
                    } else if (checkRoute(data[0], new String[]{"GET", "MEMORY"})) {
                        ArrayList<String> list = new ArrayList<String>();
                        ArrayList<ArrayList<String>> list2 = new ArrayList<ArrayList<String>>();
                        for (int i = 0; i < iteration; i++) {
                            list.add("qwertytuyiopsadfghjklzxcvbnm");
                        }
                        for (int i = 0; i < iteration; i++) {
                            list2.add(list);
                        }
                        respond("200", "Success");
                    } else {
                        respond("404", "Could not be found.");
                    }
                    clientSocket.close();
                }

                private void respond(String statusCode, String response) throws IOException {
                    clientSocket.getOutputStream().write(("HTTP/1.1 "+statusCode+" \r\n\r\n" + response).getBytes("UTF-8"));
                }

                public Boolean checkRoute(String path, String[] routeElements) {
                    for (String element:routeElements) {
                        if (!path.contains(element)) return false;
                    }
                    return true;
                }

                public void run() {
                    try {
                        route(getHttpData());
                    } catch (Exception e) { System.out.println("Error: "+ e); }
                }
            }).start();
        }
    }


}
