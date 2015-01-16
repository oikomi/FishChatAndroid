package com.yuchuan.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by haroldmiao on 2014/12/26.
 */

public class Config {
    String gateway;

    private static Config _instance;

    static {
        Gson gson = new Gson();
        FileInputStream configIn = null;
        try {
            configIn = new FileInputStream("fishchat.json");

            _instance = gson.fromJson(IOUtils.toString(configIn), Config.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(configIn);
        }
    }

    public static Config getInstance() {
        return _instance;
    }

    public String getGateway() {
        return gateway;
    }
}


