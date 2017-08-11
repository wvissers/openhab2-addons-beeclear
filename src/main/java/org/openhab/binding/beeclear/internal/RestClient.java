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
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openhab.binding.beeclear.handler.BeeClearHandler;
import org.openhab.binding.beeclear.internal.data.ActiveValues;
import org.openhab.binding.beeclear.internal.data.SoftwareVersion;
import org.openhab.binding.beeclear.internal.data.SoftwareVersionImpl;
import org.openhab.binding.beeclear.internal.data.UnsupportedVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wim Vissers - Initial contribution
 */
public class RestClient {

    private static final int CONNECTION_TIMEOUT = 20000;
    private final Logger _logger = LoggerFactory.getLogger(BeeClearHandler.class);

    // The endpoint address of the BeeClear WebAPI.
    private String _endPoint;

    // The factory to create data elements
    private DataElementFactory _factory;

    /**
     * Create a new client with the given server and port address.
     *
     * @param endPoint
     */
    public RestClient(String server, int port) {
        _endPoint = "http://" + server + ":" + port;
        _factory = DataElementFactory.getInstance();
        _logger.info("Creating RestClient with endPoint {}", _endPoint);
    }

    private String getResponse(String resourcePath) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(_endPoint + resourcePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            if (conn.getResponseCode() != 200) {
                _logger.error("Unexpected response {}", conn.getResponseCode());
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String line;
            System.out.println("Output from Server .... \n");
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private String postData(String resourcePath, String data) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(_endPoint + resourcePath);
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * The jointSpace mode resource.
     *
     * @param json
     */
    public void setMode() {
        postData("/ambilight/mode", "json string");
    }

    /**
     * The jointSpace mode resource.
     *
     * @return
     */
    public String getMode() {
        return getResponse("/ambilight/mode");
    }

    /**
     * Retrieve the software/firmware version from the BeeClear unit.
     *
     * @return
     */
    public SoftwareVersion getSoftwareVersion() {
        SoftwareVersion result = null;
        JSONParser parser = new JSONParser();
        String response = getResponse("/bc_softwareVersion");
        try {
            JSONObject obj = (JSONObject) parser.parse(response);
            result = new SoftwareVersionImpl(obj);
        } catch (ParseException e) {
            _logger.error("Invalid JSON response {}.", response);
        }
        return result;
    }

    /**
     * Retrieve the software/firmware version from the BeeClear unit.
     *
     * @return
     */
    public ActiveValues getActiveValues(SoftwareVersion softwareVersion) {
        ActiveValues result = null;
        JSONParser parser = new JSONParser();
        Instant instant = Instant.now();
        String response = getResponse("/bc_current?nu=" + instant.getEpochSecond());
        try {
            JSONObject obj = (JSONObject) parser.parse(response);
            result = _factory.createActiveValues(softwareVersion, obj);
        } catch (ParseException e) {
            _logger.error("Invalid JSON response {}.", response);
        } catch (UnsupportedVersionException e) {
            _logger.error("Unsupported software version for response {}.", response);
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
        return _factory.isSupported(softwareVersion);
    }

}
