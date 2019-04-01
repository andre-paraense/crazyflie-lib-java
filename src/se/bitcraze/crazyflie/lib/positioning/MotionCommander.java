package se.bitcraze.crazyflie.lib.positioning;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;

public class MotionCommander {
	
	private Crazyflie crazyflie;
    private float velocity = 0.2f;
    private float height = 0.3f;
    private float rate = 360.0f/5.0f;
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
    	
    	up(height, velocity);	
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
    
    public void up(float distance, float velocity) throws Exception {    	
    	moveDistance(0.0f, 0.0f, distance, velocity);
    }
    
    public void moveDistance(float distanceX, float distanceY, float distanceZ, float velocity) throws Exception{
    	float distance = (float) Math.sqrt(distanceX * distanceX + 
				distanceY * distanceY +
				distanceZ * distanceZ
				);
    	
    	float flightTime = distance / velocity;
    	
    	float velocityX = velocity * distanceX / distance;
		float velocityY = velocity * distanceY / distance;
		float velocityZ = velocity * distanceZ / distance;
		
		startLinearMotion(velocityX, velocityY, velocityZ);
		try {
			Thread.sleep((long) (flightTime*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
		
	}

    public void stop() throws Exception {
    	setVelocitySetpoint(0.0f, 0.0f, 0.0f, 0.0f);	
	}

	public void startLinearMotion(float velocityX, float velocityY, float velocityZ) throws Exception{
		setVelocitySetpoint(velocityX, velocityY, velocityZ, 0.0f);	
	}

	public void setVelocitySetpoint(float velocityX, float velocityY, float velocityZ, float rateYaw) throws Exception {
		if(!isFlying) {
			throw new Exception("Can not move on the ground. Take off first!");
		}
		
		setPointThread.setVelocitySetpoint(velocityX, velocityY, velocityZ, rateYaw);	
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
    	
    	private static final long UPDATE_PERIOD = 200L;
    	private static final int ABS_Z_INDEX = 3;
    	private Crazyflie crazyflie;
    	private float[] hoverSetpoint = {0.0f, 0.0f, 0.0f, 0.0f};
    	private float zBase = 0.0f;
    	private float zVelocity = 0.0f;
    	private float zBaseTime = 0.0f;
    	private LinkedBlockingQueue<QueueEvent> queue = new LinkedBlockingQueue<>();
    	
    	
    	public SetPointThread(Crazyflie crazyflie) {
			this.crazyflie = crazyflie;
		}

		public void setVelocitySetpoint(float velocityX, float velocityY, float velocityZ, float rateYaw) throws InterruptedException {
			queue.put(new QueueEvent(velocityX, velocityY, velocityZ, rateYaw));
			
		}

		@Override
		public void run() {
			while(true) {
				try {
					QueueEvent queueEvent = queue.poll(UPDATE_PERIOD,TimeUnit.MILLISECONDS);
					if(queueEvent != null) {
						if(queueEvent.shouldTerminate) {
							return;
						}
						
						//newSetpoint(queueEvent);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    private class QueueEvent{
    	
    	private float velocityX = 0.0f;
    	private float velocityY = 0.0f;
    	private float velocityZ = 0.0f;
    	private float rateYaw = 0.0f;
    	private boolean shouldTerminate = false;
    	
		public QueueEvent(float velocityX, float velocityY, float velocityZ, float rateYaw) {
			super();
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
			this.rateYaw = rateYaw;
		}	
		
		public QueueEvent(boolean shouldTerminate) {
			super();
			this.shouldTerminate = shouldTerminate;
		}

		public float getVelocityX() {
			return velocityX;
		}
		public float getVelocityY() {
			return velocityY;
		}
		public float getVelocityZ() {
			return velocityZ;
		}
		public float getRateYaw() {
			return rateYaw;
		}
		public boolean isShouldTerminate() {
			return shouldTerminate;
		}	
    }
}
