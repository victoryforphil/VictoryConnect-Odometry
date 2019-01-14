package com.victoryforphil.connect.odometry;

public class Kinematics{
    private double lastTimestamp;
    private Transform2d lastPos = new Transform2d();

    public Kinematics(){
    }

    public Transform2d getPose(double timestamp, double robotVelocity, double rotation){

        double dt = timestamp - lastTimestamp;
        this.lastTimestamp = timestamp;

        double forwardVelocity = robotVelocity;
        double deltaForward = forwardVelocity * dt;
        double radianHeading = Math.toRadians(rotation);
        lastPos.x += deltaForward * Math.cos(radianHeading);
        lastPos.y += deltaForward * Math.sin(radianHeading);

        
        return lastPos;

    }

    public void updatePose(double x, double y){
        lastPos.x = x;
        lastPos.y = y;
    }
}