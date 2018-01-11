/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.beeclear.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openhab.binding.beeclear.handler.BeeClearHandler;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.openhab.binding.beeclear.internal.data.SoftwareVersionImpl;
import org.openhab.binding.beeclear.internal.data.Status;
import org.openhab.binding.beeclear.internal.data.UnsupportedVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wim Vissers - Initial contribution
 */
public class RestClient {

    private static final int CONNECTION_TIMEOUT = 20000;
    private final Logger logger = LoggerFactory.getLogger(BeeClearHandler.class);

    // The endpoint address of the BeeClear WebAPI.
    private String endPoint;

    // The factory to create data elements
    private DataElementFactory factory;

    /**
     * Create a new client with the given server and port address.
     *
     * @param endPoint
     */
    public RestClient(String server, int port) {
        endPoint = "http://" + server + ":" + port;
        factory = DataElementFactory.getInstance();
        logger.info("Creating RestClient with endPoint {}", endPoint);
    }

    private String getResponse(String resourcePath) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL(endPoint + resourcePath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        if (conn.getResponseCode() != 200) {
            logger.error("Unexpected response {}", conn.getResponseCode());
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String line;
        System.out.println("Output from Server .... \n");
        while ((line = br.readLine()) != null) {
            output.append(line);
        }
        conn.disconnect();
        return output.toString();
    }

    private String postData(String resourcePath, String data) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL(endPoint + resourcePath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Content-length", "" + data.length());
        conn.setDoOutput(true);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        OutputStream out = conn.getOutputStream();
        out.write(data.getBytes());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String line;
        System.out.println("Output from Server .... \n");
        while ((line = br.readLine()) != null) {
            output.append(line);
        }
        conn.disconnect();
        return output.toString();
    }

    /**
     * Retrieve the software/firmware version from the BeeClear unit.
     *
     * @return
     */
    public SoftwareVersion getSoftwareVersion() throws IOException {
        SoftwareVersion result = null;
        JSONParser parser = new JSONParser();
        String response = getResponse("/bc_softwareVersion");
        try {
            JSONObject obj = (JSONObject) parser.parse(response);
            result = new SoftwareVersionImpl(obj);
        } catch (ParseException e) {
            logger.error("Invalid JSON response {}.", response);
        }
        return result;
    }

    /**
     * Retrieve the currently active values from the BeeClear unit.
     *
     * @return
     */
    public ActiveValues getActiveValues(SoftwareVersion softwareVersion) throws IOException {
        ActiveValues result = null;
        JSONParser parser = new JSONParser();
        Instant instant = Instant.now();
        String response = getResponse("/bc_current?nu=" + instant.getEpochSecond());
        try {
            JSONObject obj = (JSONObject) parser.parse(response);
            result = factory.createActiveValues(softwareVersion, obj);
        } catch (ParseException e) {
            logger.error("Invalid JSON response {}.", response);
        } catch (UnsupportedVersionException e) {
            logger.error("Unsupported software version for response {}.", response);
        }
        return result;
    }

    /**
     * Retrieve the current status from the BeeClear unit.
     *
     * @return
     */
    public Status getStatus(SoftwareVersion softwareVersion) throws IOException {
        Status result = null;
        JSONParser parser = new JSONParser();
        String response = getResponse("/bc_status");
        try {
            JSONObject obj = (JSONObject) parser.parse(response);
            result = factory.createStatus(softwareVersion, obj);
        } catch (ParseException e) {
            logger.error("Invalid JSON response {}.", response);
        } catch (UnsupportedVersionException e) {
            logger.error("Unsupported software version for response {}.", response);
        }
        return result;
    }

    /**
     * Determine if a version is supported.
     *
     * @param softwareVersion
     * @return
     */
    public boolean isSupported(SoftwareVersion softwareVersion) {
        return factory.isSupported(softwareVersion);
    }

}
