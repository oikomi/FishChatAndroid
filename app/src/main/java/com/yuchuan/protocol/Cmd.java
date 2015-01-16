package com.yuchuan.protocol;

import java.util.ArrayList;

/**
 * Created by haroldmiao on 2015/1/16.
 */
public class Cmd {
    private String CmdName;
    private ArrayList Args = new ArrayList();

    public static final String REQ_MSG_SERVER_CMD                    = "REQ_MSG_SERVER";
    public static final String SELECT_MSG_SERVER_FOR_CLIENT_CMD      = "SELECT_MSG_SERVER_FOR_CLIENT";

    public Cmd(){

    }

    public String getCmdName() {
        return CmdName;
    }

    public void setCmdName(String cmdName) {
        CmdName = cmdName;
    }

    public void addArg(String arg) {
        this.Args.add(arg);
    }

    @Override
    public String toString() {
        String out = null;
        for (int i = 0; i < Args.size(); i++) out += Args.get(i);

        return CmdName + out;
    }
}
