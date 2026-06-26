package io.github.pixeldungeonmultiplayer.shattered.client.network;

public final class Protocol {
    public static final String NAME = "spdmp";
    public static final int MIN_VERSION = 1;
    public static final int VERSION = 1;

    public static final String FIELD_PACKET_TYPE = "packet_type";
    public static final String FIELD_PROTOCOL = "protocol";
    public static final String FIELD_VERSION = "protocol_version";
    public static final String FIELD_SERVER_ID = "server_id";

    public static final String PACKET_HANDSHAKE = "handshake";
    public static final String PACKET_STATUS_REQUEST = "status_request";
    public static final String PACKET_SERVER_STATUS = "server_status";
    public static final String PACKET_JOIN = "join";
    public static final String PACKET_DISCONNECT = "disconnect";
    public static final String PACKET_ACTIONS_BATCH = "actions_batch";
    public static final String PACKET_CLIENT_COMMAND = "client_command";

    private Protocol() {
    }
}
