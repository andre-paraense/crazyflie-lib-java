package se.bitcraze.crazyflie.lib.positioning;

import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;
import se.bitcraze.crazyflie.lib.crtp.HoverPacket;
import se.bitcraze.crazyflie.lib.crtp.StopPacket;

public class MotionCommander {

	private Crazyflie crazyflie;
	private float velocity = 0.2f;
	private float height = 0.3f;
	private float rate = 360.0f/5.0f;
	private float angle = 360.0f;
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
		takeOff(height, velocity);
	}

	public void takeOff(float height) throws java.lang.Exception {
		takeOff(height, velocity);
	}

	public void takeOff(float velocity, float height) throws java.lang.Exception {
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

	public void land() throws Exception {
		land(velocity);
	}

	public void land(float velocity) throws Exception {
		if(isFlying) {
			down(setPointThread.getHeight(), velocity);
			setPointThread.stop();
			crazyflie.sendPacket(new StopPacket());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			crazyflie.disconnect();
			isFlying = false;
		}
	}
	
	public void down(float height) throws Exception {
		down(height, velocity);
	}

	public void down(float height, float velocity) throws Exception {
		moveDistance(0.0f, 0.0f, -height, velocity);
	}
	
	public void up(float height) throws Exception {
		up(height, velocity);
	}

	public void up(float height, float velocity) throws Exception {
		moveDistance(0.0f, 0.0f, height, velocity);
	}
	
	public void left(float distance) throws Exception {
		left(distance, velocity);
	}

	public void left(float distance, float velocity) throws Exception {
		moveDistance(0.0f, distance, 0.0f, velocity);
	}
	
	public void right(float distance) throws Exception {
		right(distance, velocity);
	}

	public void right(float distance, float velocity) throws Exception {
		moveDistance(0.0f, -distance, 0.0f, velocity);
	}
	
	public void forward(float distance) throws Exception {
		forward(distance, velocity);
	}

	public void forward(float distance, float velocity) throws Exception {
		moveDistance(distance, 0.0f, 0.0f, velocity);
	}
	
	public void back(float distance) throws Exception {
		back(distance, velocity);
	}

	public void back(float distance, float velocity) throws Exception {
		moveDistance(-distance, 0.0f, 0.0f, velocity);
	}

	public void turnLeft(float angle)  throws Exception {
		turnLeft(angle, rate);
	}

	public void turnLeft(float angle, float rate)  throws Exception {
		float flightTime = angle/rate;
		startTurnLeft(rate);
		try {
			Thread.sleep((long) (flightTime*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
	}

	public void turnRight(float angle, float rate)  throws Exception {
		float flightTime = angle / rate;
		startTurnRight(rate);
		try {
			Thread.sleep((long) (flightTime*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
	}

	public void circleLeft(float radius) throws Exception{
		circleLeft(radius, velocity, angle);
	}

	public void circleLeft(float radius, float velocity, float angle) throws Exception{
		float distance = (float) (2.0f * radius * Math.PI * angle / 360.0f);
		float flightTime = distance / velocity;

		startCircleLeft(radius, velocity);
		try {
			Thread.sleep((long) (flightTime*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
	}

	public void circleRight(float radius, float velocity, float angle) throws Exception{
		float distance = (float) (2.0f * radius * Math.PI * angle / 360.0f);
		float flightTime = distance / velocity;

		startCircleRight(radius, velocity);
		try {
			Thread.sleep((long) (flightTime*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop();
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

	public void startLeft() throws Exception{
		startLeft(velocity);
	}

	public void startLeft(float velocity) throws Exception{
		startLinearMotion(0.0f, velocity, 0.0f);
	}

	public void startRight() throws Exception{
		startRight(velocity);
	}

	public void startRight(float velocity) throws Exception{
		startLinearMotion(0.0f, -velocity, 0.0f);
	}

	public void startForward() throws Exception{
		startForward(velocity);
	}

	public void startForward(float velocity) throws Exception{
		startLinearMotion(velocity, 0.0f, 0.0f);
	}

	public void startBack() throws Exception{
		startBack(velocity);
	}

	public void startBack(float velocity) throws Exception{
		startLinearMotion(-velocity, 0.0f, 0.0f);
	}

	public void startUp() throws Exception{
		startUp(velocity);
	}

	public void startUp(float velocity) throws Exception{
		startLinearMotion(0.0f, 0.0f, velocity);
	}

	public void startDown() throws Exception{
		startDown(velocity);
	}

	public void startDown(float velocity) throws Exception{
		startLinearMotion(0.0f, 0.0f, -velocity);
	}

	public void stop() throws Exception {
		setVelocitySetpoint(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void startCircleLeft(float radius, float velocity) throws Exception{
		float circumference = (float) (2.0f * radius * Math.PI);
		float rate = 360.0f * velocity / circumference;
		setVelocitySetpoint(velocity, 0.0f, 0.0f, -rate);
	}

	public void startCircleRight(float radius, float velocity) throws Exception{
		float circumference = (float) (2.0f * radius * Math.PI);
		float rate = 360.0f * velocity / circumference;
		setVelocitySetpoint(velocity, 0.0f, 0.0f, rate);
	}

	public void startTurnLeft(float rate) throws Exception{
		setVelocitySetpoint(0.0f, 0.0f, 0.0f, -rate);
	}

	public void startTurnRight(float rate) throws Exception{
		setVelocitySetpoint(0.0f, 0.0f, 0.0f, rate);
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
		private long zBaseTime = 0L;
		private LinkedBlockingQueue<QueueEvent> queue = new LinkedBlockingQueue<>();

		public SetPointThread(Crazyflie crazyflie) {
			this.crazyflie = crazyflie;
		}

		public void setVelocitySetpoint(float velocityX, float velocityY, float velocityZ, float rateYaw) throws InterruptedException {
			queue.put(new QueueEvent(velocityX, velocityY, velocityZ, rateYaw));
		}

		public void stop(){
			try {
				queue.put(new QueueEvent(true));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public float getHeight(){
			return hoverSetpoint[ABS_Z_INDEX];
		}

		@Override
		public void run() {
			while(true) {
				try {
					QueueEvent queueEvent = queue.poll(UPDATE_PERIOD,TimeUnit.MILLISECONDS);
					if(queueEvent != null) {
						if(queueEvent.isShouldTerminate()) {
							return;
						}
						newSetpoint(queueEvent);
						updateZinSetpoint();
						crazyflie.sendPacket(new HoverPacket(hoverSetpoint[0], hoverSetpoint[1], hoverSetpoint[2], hoverSetpoint[3]));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void newSetpoint(QueueEvent queueEvent){
			zBase = currentZ();
			zVelocity = queueEvent.getVelocityZ();
			zBaseTime = Instant.now().getEpochSecond();

			hoverSetpoint[0] = queueEvent.getVelocityX();
			hoverSetpoint[1] = queueEvent.getVelocityY();
			hoverSetpoint[2] = queueEvent.getRateYaw();
			hoverSetpoint[3] = zBase;
		}

		private float currentZ(){
			long now = Instant.now().getEpochSecond();

			return zBase + zVelocity * (now - zBaseTime);
		}

		private void updateZinSetpoint(){
			hoverSetpoint[ABS_Z_INDEX] = currentZ();
		}
	}

	class QueueEvent{

		private float velocityX = 0.0f;
		private float velocityY = 0.0f;
		private float velocityZ = 0.0f;
		private float rateYaw = 0.0f;
		private boolean shouldTerminate = false;

		public QueueEvent(float velocityX, float velocityY, float velocityZ, float rateYaw) {
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
			this.rateYaw = rateYaw;
		}

		public QueueEvent(boolean shouldTerminate) {
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
