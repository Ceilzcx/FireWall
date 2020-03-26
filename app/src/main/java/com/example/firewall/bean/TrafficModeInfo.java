package com.example.firewall.bean;

public class TrafficModeInfo {
    private String ifname;
    private long rx_bytes;
    private int rx_packets;
    private long tx_bytes;
    private int tx_packets;

    public TrafficModeInfo(){}

    public long getRx_bytes() {
        return rx_bytes;
    }

    public int getRx_packets() {
        return rx_packets;
    }

    public long getTx_bytes() {
        return tx_bytes;
    }

    public int getTx_packets() {
        return tx_packets;
    }

    public String getIfname() {
        return ifname;
    }

    public void setIfname(String ifname) {
        this.ifname = ifname;
    }

    public void setRx_bytes(long rx_bytes) {
        this.rx_bytes = rx_bytes;
    }

    public void setRx_packets(int rx_packets) {
        this.rx_packets = rx_packets;
    }

    public void setTx_bytes(long tx_bytes) {
        this.tx_bytes = tx_bytes;
    }

    public void setTx_packets(int tx_packets) {
        this.tx_packets = tx_packets;
    }
}
