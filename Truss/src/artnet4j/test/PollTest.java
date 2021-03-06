/*
 * This file is part of artnet4j.
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * artnet4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * artnet4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with artnet4j. If not, see <http://www.gnu.org/licenses/>.
 */

package artnet4j.test;

import java.net.SocketException;
import java.util.List;
import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

public class PollTest implements ArtNetDiscoveryListener {

    public static void main(String[] args) {
        new PollTest().test();
    }

    private ArtNetNode netLynx;

    private int sequenceID;

    @Override
    public void discoveredNewNode(ArtNetNode node) {
        if (netLynx == null) {
            netLynx = node;
            System.out.println("found net lynx");
        }
    }

    @Override
    public void discoveredNodeDisconnected(ArtNetNode node) {
        System.out.println("node disconnected: " + node);
        if (node == netLynx) {
            netLynx = null;
        }
    }

    @Override
    public void discoveryCompleted(List<ArtNetNode> nodes) {
        System.out.println(nodes.size() + " nodes found:");
        System.out.println(nodes);
        for (ArtNetNode n : nodes) {
            System.out.println(n);
        }
    }

    @Override
    public void discoveryFailed(Throwable t) {
        System.out.println("discovery failed");
    }

    private void test() {
        ArtNet artnet = new ArtNet();
        try {
            artnet.start();
            artnet.getNodeDiscovery().addListener(this);
            artnet.setBroadCastAddress("255.255.255.255");
            artnet.startNodeDiscovery();
            int a=0;
            while (true) {
            	if(a == 255){
            		a=0;
            	}
                if (netLynx != null) {
                    ArtDmxPacket dmx = new ArtDmxPacket();
                    dmx.setUniverse(netLynx.getSubNet(), netLynx.getDmxOuts()[0]);
                    dmx.setSequenceID(sequenceID % 255);
                    byte[] buffer = new byte[510];
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] =
                                (byte) (a);
                    }
                    dmx.setDMX(buffer, buffer.length);
                    artnet.unicastPacket(dmx, netLynx.getIPAddress());
                    sequenceID++;
                }
                a++;
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
