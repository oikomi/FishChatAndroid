package com.yuchuan.protocol;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by haroldmiao on 2015/1/16.
 */
public class Cmd implements Serializable {
    private String CmdName;
    private ArrayList Args = new ArrayList();

    public static final String REQ_MSG_SERVER_CMD                    = "REQ_MSG_SERVER";
    public static final String SELECT_MSG_SERVER_FOR_CLIENT_CMD      = "SELECT_MSG_SERVER_FOR_CLIENT";
    public static final String SEND_CLIENT_ID_CMD                    = "SEND_CLIENT_ID";
    public static final String SEND_CLIENT_ID_FOR_TOPIC_CMD          = "SEND_CLIENT_ID_FOR_TOPIC";
    //SEND_MESSAGE_P2P send2ID send2msg
    public static final String SEND_MESSAGE_P2P_CMD                  = "SEND_MESSAGE_P2P";
    //RESP_MESSAGE_P2P  msg fromID
    public static final String RESP_MESSAGE_P2P_CMD                  = "RESP_MESSAGE_P2P";
    public static final String ROUTE_MESSAGE_P2P_CMD                 = "ROUTE_MESSAGE_P2P";
    public static final String CREATE_TOPIC_CMD                      = "CREATE_TOPIC";
    //JOIN_TOPIC TOPIC_NAME CLIENT_ID
    public static final String JOIN_TOPIC_CMD                        = "JOIN_TOPIC";
    public static final String LOCATE_TOPIC_MSG_ADDR_CMD             = "LOCATE_TOPIC_MSG_ADDR";
    public static final String SEND_MESSAGE_TOPIC_CMD                = "SEND_MESSAGE_TOPIC";
    public static final String RESP_MESSAGE_TOPIC_CMD                = "RESP_MESSAGE_TOPIC";

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

    public Object getArg(int pos) {
        return this.Args.get(pos);
    }

    @Override
    public String toString() {
        String out = null;
        for (int i = 0; i < Args.size(); i++) out += Args.get(i);

        return CmdName + out;
    }
}
