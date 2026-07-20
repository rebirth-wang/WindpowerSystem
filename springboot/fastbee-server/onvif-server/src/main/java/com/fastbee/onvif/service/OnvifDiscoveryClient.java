package com.fastbee.onvif.service;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fastbee.onvif.domain.OnvifDiscoveryDevice;

/**
 * Lightweight ONVIF WS-Discovery client.
 */
@Slf4j
@Component
public class OnvifDiscoveryClient {

    private static final String MULTICAST_ADDRESS = "239.255.255.250";
    private static final int MULTICAST_PORT = 3702;
    private static final int BUFFER_SIZE = 16 * 1024;

    public List<OnvifDiscoveryDevice> discover(int timeoutMillis) {
        Map<String, OnvifDiscoveryDevice> devices = new LinkedHashMap<>();
        long deadline = System.currentTimeMillis() + Math.max(timeoutMillis, 1000);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(Math.max(timeoutMillis, 1000));

            byte[] payload = buildProbeMessage().getBytes(StandardCharsets.UTF_8);
            DatagramPacket probe = new DatagramPacket(
                    payload,
                    payload.length,
                    InetAddress.getByName(MULTICAST_ADDRESS),
                    MULTICAST_PORT);
            socket.send(probe);

            while (System.currentTimeMillis() < deadline) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(response);
                    OnvifDiscoveryDevice device = parseResponse(response);
                    if (device != null && device.getIp() != null && device.getPort() != null) {
                        devices.put(device.getIp() + ":" + device.getPort(), device);
                    }
                } catch (java.net.SocketTimeoutException e) {
                    break;
                } catch (Exception e) {
                    log.debug("[ONVIF discovery] Ignore invalid response: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("[ONVIF discovery] Probe failed: {}", e.getMessage());
        }
        return new ArrayList<>(devices.values());
    }

    private String buildProbeMessage() {
        String uuid = "uuid:" + UUID.randomUUID();
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<e:Envelope xmlns:e=\"http://www.w3.org/2003/05/soap-envelope\" "
                + "xmlns:w=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" "
                + "xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\" "
                + "xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\">"
                + "<e:Header>"
                + "<w:MessageID>" + uuid + "</w:MessageID>"
                + "<w:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</w:To>"
                + "<w:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</w:Action>"
                + "</e:Header>"
                + "<e:Body>"
                + "<d:Probe><d:Types>dn:NetworkVideoTransmitter</d:Types></d:Probe>"
                + "</e:Body>"
                + "</e:Envelope>";
    }

    private OnvifDiscoveryDevice parseResponse(DatagramPacket packet) throws Exception {
        String xml = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception ignored) {
            // Some XML parsers do not support all hardening flags.
        }

        Document document = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        String xaddrs = firstText(document, "XAddrs");
        String xaddr = firstHttpUrl(xaddrs);
        if (xaddr == null) {
            return null;
        }

        URI uri = URI.create(xaddr);
        if (uri.getHost() == null) {
            return null;
        }

        OnvifDiscoveryDevice device = new OnvifDiscoveryDevice();
        device.setIp(uri.getHost());
        device.setPort(uri.getPort() > 0 ? uri.getPort() : 80);
        device.setXaddr(xaddr);
        device.setTypes(firstText(document, "Types"));
        device.setScopes(firstText(document, "Scopes"));
        return device;
    }

    private String firstText(Document document, String localName) {
        NodeList nodes = document.getElementsByTagNameNS("*", localName);
        if (nodes.getLength() == 0 || nodes.item(0) == null) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }

    private String firstHttpUrl(String xaddrs) {
        if (xaddrs == null || xaddrs.trim().isEmpty()) {
            return null;
        }
        String[] urls = xaddrs.trim().split("\\s+");
        for (String url : urls) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }
        }
        return null;
    }
}
