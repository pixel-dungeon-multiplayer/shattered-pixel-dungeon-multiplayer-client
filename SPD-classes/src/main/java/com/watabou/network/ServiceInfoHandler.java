package com.watabou.network;

public abstract class ServiceInfoHandler {
    protected static final String[] serviceTypes = new String[]{"_spdmp._tcp."/*, "_mppd._tcp."*/};

    protected final ServiceInfoListener listener;
    public abstract void startDiscovery();
    public abstract void stopDiscovery();

    public ServiceInfoHandler(ServiceInfoListener listener) {
        this.listener = listener;
    }
    public void onServiceResolved(ServiceInfo service){
        listener.onServiceResolved(service);
    }
    public void onServiceLost(ServiceInfo service){
        listener.onServiceLost(service);
    }
    public void onServiceFound(ServiceInfo service){
        listener.onServiceFound(service);
    }

}
