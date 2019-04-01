package se.bitcraze.crazyflie.lib.positioning;

import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;

public class MotionCommander {
	
	private Crazyflie crazyflie;
    private float velocity = 0.2f;
    private float height = 0.3f;
    private float rate = 360.0f/5f;
    private boolean isFlying = false;
    private SetPointThread setPointThread;
    
    public MotionCommander(Crazyflie crazyflie) {
    	this.crazyflie = crazyflie;
    	isFlying = false;
    	setPointThread = null;
    }
    
    public MotionCommander(Crazyflie crazyflie, float height) {
    	this.crazyflie = crazyflie;
    	this.height = height;
    	isFlying = false;
    	setPointThread = null;
    }
    
    
    public void takeOff() throws java.lang.Exception {
    	if(isFlying) {
    		throw new java.lang.Exception("Already flying");
    	}
    	
    	if(!crazyflie.isConnected()) {
    		throw new java.lang.Exception("Crazyflie is not connected");
    	}
    	
    	isFlying = true;
    	
    	resetPositionEstimator();
    	
    	setPointThread = new SetPointThread(crazyflie);
    	Thread thread = new Thread(setPointThread);
    	thread.start();
    	
    	up();	
    }
    
    public void takeOff(float height) throws java.lang.Exception {
    	this.height = height;
    	takeOff();
    }
    
    public void takeOff(float velocity, float height) throws java.lang.Exception {
    	this.velocity = velocity;
    	this.height = height;
    	takeOff();
    }
    
    public void up() {
    	
    	//TODO
    	
    }
    
    public void resetPositionEstimator() {
    	crazyflie.getParam().setValue("kalman.resetEstimation", 1);
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	crazyflie.getParam().setValue("kalman.resetEstimation", 0);
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private class SetPointThread implements Runnable{
    	
    	private Crazyflie crazyflie;
    	
    	public SetPointThread(Crazyflie crazyflie) {
			this.crazyflie = crazyflie;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
}
