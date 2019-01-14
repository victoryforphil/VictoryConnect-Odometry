package com.victoryforphil.connect.odometry;
import com.victoryforphil.victoryconnect.*;
import com.victoryforphil.victoryconnect.listeners.ClientListener;
import com.victoryforphil.victoryconnect.listeners.MDNSListener;
import com.victoryforphil.victoryconnect.listeners.PacketListener;
import com.victoryforphil.victoryconnect.networking.Packet;
public class Main{
    private static Client vcClient = new Client("odometry", "Odometry");
    private static Kinematics kinematics = new Kinematics();
    private static boolean started = false;

    public static void main(String[] args){

        vcClient.enableMDNS(new MDNSListener(){
            @Override
            public void onService(String type, String ip, String port) {
                if(type == "TCP" && !started){
                    vcClient.enableTCP(ip, port);
                    started = true;
                }
            }
        });

        vcClient.setListener(new ClientListener(){
        
            @Override
            public void ready() {
                vcClient.enableASAP();
                vcClient.newTopic("Odometry Location", "odom/loc", "TCP");
                vcClient.setTopic("odom/loc", new String[] {"0","0"});
                

                vcClient.newTopic("Odometry Heading", "odom/heading", "TCP");
                vcClient.setTopic("odom/heading", new String[] {"0"});

                vcClient.newTopic("Odometry Input", "odom/input", "TCP");
                vcClient.setTopic("odom/input", new String[] {"0,0", Double.toString(System.currentTimeMillis())});

                vcClient.subscribe("odom/input", new PacketListener(){
                    @Override
                    public void onCommand(Packet packet) {
                        if(packet.data.length < 3){
                            return;
                        }

                        double vel = Double.parseDouble(packet.data[0]);
                        double heading = Double.parseDouble(packet.data[1]);
                        double timestamp = Double.parseDouble(packet.data[2]);
                        
                        Transform2d pose =  kinematics.getPose(timestamp, vel, heading);
                        vcClient.setTopic("odom/loc", new String[] {Double.toString(pose.x), Double.toString(pose.y)});
                        vcClient.setTopic("odom/heading", new String[] {Double.toString(heading)});
                    }
                });

                vcClient.registerCommand("odometry_set", new PacketListener(){
                
                    @Override
                    public void onCommand(Packet packet) {
                        double x = Double.parseDouble(packet.data[0]);
                        double y = Double.parseDouble(packet.data[1]);

                        kinematics.updatePose(x,y);
                    }
                });
            }
        });
        
    }

}