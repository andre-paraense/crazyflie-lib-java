/**
 * 
 */
package se.bitcraze.crazyflie.lib.examples;

import se.bitcraze.crazyflie.lib.crazyflie.ConnectionAdapter;
import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;
import se.bitcraze.crazyflie.lib.crazyradio.ConnectionData;
import se.bitcraze.crazyflie.lib.crazyradio.Crazyradio;
import se.bitcraze.crazyflie.lib.crazyradio.RadioDriver;
import se.bitcraze.crazyflie.lib.positioning.MotionCommander;
import se.bitcraze.crazyflie.lib.usb.UsbLinkJava;

/**
 * 
 *
 */
public class MotionCommanderExample {
	
	private Crazyflie crazyflie;
	
    /**
     * Initialize and run the example with the specified link_uri
     */
    public MotionCommanderExample(ConnectionData connectionData) {

        crazyflie = new Crazyflie(new RadioDriver(new UsbLinkJava()));

        crazyflie.getDriver().addConnectionListener(new ConnectionAdapter() {

            /**
             * This callback is called from the Crazyflie API when a Crazyflie
             + has been connected and the TOCs have been downloaded.
             */
            @Override
            public void connected() {
                System.out.println("CONNECTED");
            }

            @Override
            public void setupFinished() {
                System.out.println("SETUP FINISHED");
                try {
                    sendMotionCommands();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }          

			/*
             * Callback when the Crazyflie is disconnected (called in all cases)
             */
            @Override
            public void disconnected() {
                System.out.println("DISCONNECTED");
            }

            /*
             * Callback when connection initial connection fails (i.e no Crazyflie at the specified address)
             */
            @Override
            public void connectionFailed(String msg) {
                System.out.println("CONNECTION FAILED: " + msg);
            }

            /**
             * Callback when disconnected after a connection has been made (i.e Crazyflie moves out of range)
             *
             * @param connectionInfo
             */
            @Override
            public void connectionLost(String msg) {
                System.out.println("CONNECTION LOST: " + msg);
            }

        });

        crazyflie.setConnectionData(connectionData);
        crazyflie.connect();

        System.out.println("Connection to " + connectionData);
    }
    
    private void sendMotionCommands() throws Exception {
		
    	MotionCommander motionCommander = new MotionCommander(crazyflie);
    	
    	System.out.println("Sending take off command");
    	motionCommander.takeOff();
    	sleep(1000L);
    	
    	System.out.println("Sending forward command");
    	motionCommander.forward(0.8f);
    	sleep(1000L);
    	System.out.println("Sending back command");
    	motionCommander.back(0.8f);
    	sleep(1000L);
    	
    	System.out.println("Sending up command");
    	motionCommander.up(0.5f);
    	sleep(1000L);
    	System.out.println("Sending down command");
    	motionCommander.down(0.5f);
    	sleep(1000L);
    	
    	System.out.println("Sending right command");
    	motionCommander.right(0.5f, 0.8f);
    	sleep(1000L);
    	System.out.println("Sending left command");
    	motionCommander.left(0.5f, 0.4f);
    	sleep(1000l);
    	
//    	System.out.println("Sending circle right command");
//    	motionCommander.circleRight(0.5f, 0.5f, 180);
//    	sleep(1000L);
    	
    	System.out.println("Sending turn left command");
    	motionCommander.turnLeft(90);
    	sleep(1000l);
    	
    	System.out.println("Move distance command");
    	motionCommander.moveDistance(-1.0f, 0.0f, 0.5f, 0.6f);
    	sleep(1000l);
    	
//    	System.out.println("Sending start left command");
//    	motionCommander.startLeft(0.5f);
//    	
//    	for(int i=0; i < 5; i++) {
//    		System.out.println("Doing other work");
//    		sleep(200L);
//    	}
//    	
    	System.out.println("Sending stop command");
    	motionCommander.stop();
    	sleep(1000l);
    	
    	System.out.println("Sending land command");
    	motionCommander.land();
    	
	}
    
    private void sleep(long timeMS) {
    	try {
			Thread.sleep(timeMS);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
    }
	
	public static void main(String[] args) {
        new MotionCommanderExample(new ConnectionData(80, Crazyradio.DR_2MPS));
    }
}
