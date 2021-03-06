package se.bitcraze.crazyflie.lib.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import se.bitcraze.crazyflie.lib.crazyflie.ConnectionAdapter;
import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;
import se.bitcraze.crazyflie.lib.crazyradio.ConnectionData;
import se.bitcraze.crazyflie.lib.crazyradio.Crazyradio;
import se.bitcraze.crazyflie.lib.crazyradio.RadioDriver;
import se.bitcraze.crazyflie.lib.log.LogConfig;
import se.bitcraze.crazyflie.lib.log.LogListener;
import se.bitcraze.crazyflie.lib.log.Logg;
import se.bitcraze.crazyflie.lib.toc.VariableType;
import se.bitcraze.crazyflie.lib.usb.UsbLinkJava;


/**
 * Simple example that connects to the first Crazyflie found, logs the Stabilizer
 * and prints it to the console. After 10s the application disconnects and exits.
 *
 */
public class LoggingSensorsExample extends ConnectionAdapter{

    //# Only output errors from the logging framework
    //logging.basicConfig(level=logging.ERROR)

    //Variable used to keep main loop occupied until disconnect
    private boolean mConnected = true;
    private Crazyflie mCrazyflie;

    /**
     * Initialize and run the example with the specified connection data
     *
     * @param connectionData
     */
    public LoggingSensorsExample(ConnectionData connectionData) {
        // Create a Crazyflie object without specifying any cache dirs
        mCrazyflie = new Crazyflie(new RadioDriver(new UsbLinkJava()));
        //TODO: do not use cache

        // Connect some callbacks from the Crazyflie API
        mCrazyflie.getDriver().addConnectionListener(this);

        System.out.println("Connecting to " + connectionData);

        // Try to connect to the Crazyflie
        mCrazyflie.setConnectionData(connectionData);
        mCrazyflie.connect();
    }

    public boolean isConnected() {
        return this.mConnected;
    }

    private void setConnected(boolean connected) {
        this.mConnected = connected;
    }

    /**
     * This callback is called form the Crazyflie API when a Crazyflie
     * has been connected and the TOCs have been downloaded.
     *
     * @param connectionInfo
     */
    @Override
    public void setupFinished() {
        System.out.println("Setup finished");

        // The definition of the logconfig can be made before the setup is finished
//        final LogConfig lc = new LogConfig("battery", 100);
//        lc.addVariable("pm.state", VariableType.INT16_T);// [BATTERY, CHARGING, CHARGED, LOW_POWER] = list(range(4))
//        lc.addVariable("pm.vbat", VariableType.FLOAT);//value in volts
        
        final LogConfig lc2 = new LogConfig("multiranger", 100);
        lc2.addVariable("range.front", VariableType.FLOAT);
        lc2.addVariable("range.back", VariableType.FLOAT);
        lc2.addVariable("range.left", VariableType.FLOAT);
        lc2.addVariable("range.right", VariableType.FLOAT);
        lc2.addVariable("range.up", VariableType.FLOAT);
        lc2.addVariable("range.zrange", VariableType.FLOAT);
        
//        final LogConfig lc3 = new LogConfig("stabilizer", 100);
//        lc3.addVariable("stabilizer.roll", VariableType.FLOAT);
//        lc3.addVariable("stabilizer.pitch", VariableType.FLOAT);
//        lc3.addVariable("stabilizer.yaw", VariableType.FLOAT);
//        lc3.addVariable("stabilizer.thrust", VariableType.FLOAT);
//        
//        final LogConfig lc4 = new LogConfig("stateEstimate", 100);
//        lc4.addVariable("stateEstimate.x", VariableType.FLOAT);
//        lc4.addVariable("stateEstimate.y", VariableType.FLOAT);
//        lc4.addVariable("stateEstimate.z", VariableType.FLOAT);

        /**
         *  Adding the configuration cannot be done until a Crazyflie is connected and
         *  the setup is finished, since we need to check that the variables we
         *  would like to log are in the TOC.
         */

        final Logg logg = this.mCrazyflie.getLogg();

        if (logg != null) {

//        	logg.addConfig(lc); 
//
//            logg.addLogListener(new LogListener() {
//
//                public void logConfigAdded(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc.getName())){
//                        String msg = "";
//                        if(logConfig.isAdded()) {
//                            msg = "' added";
//                        } else {
//                            msg = "' deleted";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logConfigError(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc.getName())){
//                		System.err.println("Error when logging '" + logConfig.getName() + "': " + logConfig.getErrNo());
//                	}                   
//                }
//
//                public void logConfigStarted(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc.getName())){
//                        String msg = "";
//                        if(logConfig.isStarted()) {
//                            msg = "' started";
//                        } else {
//                            msg = "' stopped";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logDataReceived(LogConfig logConfig, Map<String, Number> data, int timestamp) {
//                	if(logConfig.getName().equalsIgnoreCase(lc.getName())){
//                        System.out.println("timestamp: " + timestamp);
//                        for (Entry<String, Number> entry : data.entrySet()) {
//                            System.out.print("\t" + entry.getKey() + ": " + entry.getValue());
//                        }
//                        System.out.println();
//                	}
//                }
//
//            });
//
//            logg.start(lc);

            logg.addConfig(lc2); 
            
            logg.addLogListener(new LogListener() {

                public void logConfigAdded(LogConfig logConfig) {
                	if(logConfig.getName().equalsIgnoreCase(lc2.getName())){
                        String msg = "";
                        if(logConfig.isAdded()) {
                            msg = "' added";
                        } else {
                            msg = "' deleted";
                        }
                        System.out.println("LogConfig '" + logConfig.getName() + msg);
                	}
                }

                public void logConfigError(LogConfig logConfig) {
                	if(logConfig.getName().equalsIgnoreCase(lc2.getName())){
                		System.err.println("Error when logging '" + logConfig.getName() + "': " + logConfig.getErrNo());
                	}                   
                }

                public void logConfigStarted(LogConfig logConfig) {
                	if(logConfig.getName().equalsIgnoreCase(lc2.getName())){
                        String msg = "";
                        if(logConfig.isStarted()) {
                            msg = "' started";
                        } else {
                            msg = "' stopped";
                        }
                        System.out.println("LogConfig '" + logConfig.getName() + msg);
                	}
                }

                public void logDataReceived(LogConfig logConfig, Map<String, Number> data, int timestamp) {
                	if(logConfig.getName().equalsIgnoreCase(lc2.getName())){
                        System.out.println("timestamp: " + timestamp);
                        for (Entry<String, Number> entry : data.entrySet()) {
                            System.out.print("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                        System.out.println();
                	}
                }

            });

            logg.start(lc2);
            
            
//            logg.addConfig(lc3); 
//            
//            logg.addLogListener(new LogListener() {
//
//                public void logConfigAdded(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc3.getName())){
//                        String msg = "";
//                        if(logConfig.isAdded()) {
//                            msg = "' added";
//                        } else {
//                            msg = "' deleted";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logConfigError(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc3.getName())){
//                		System.err.println("Error when logging '" + logConfig.getName() + "': " + logConfig.getErrNo());
//                	}                   
//                }
//
//                public void logConfigStarted(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc3.getName())){
//                        String msg = "";
//                        if(logConfig.isStarted()) {
//                            msg = "' started";
//                        } else {
//                            msg = "' stopped";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logDataReceived(LogConfig logConfig, Map<String, Number> data, int timestamp) {
//                	if(logConfig.getName().equalsIgnoreCase(lc3.getName())){
//                        System.out.println("timestamp: " + timestamp);
//                        for (Entry<String, Number> entry : data.entrySet()) {
//                            System.out.print("\t" + entry.getKey() + ": " + entry.getValue());
//                        }
//                        System.out.println();
//                	}
//                }
//
//            });
//
//            logg.start(lc3);
//            
//            logg.addConfig(lc4); 
//            
//            logg.addLogListener(new LogListener() {
//
//                public void logConfigAdded(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc4.getName())){
//                        String msg = "";
//                        if(logConfig.isAdded()) {
//                            msg = "' added";
//                        } else {
//                            msg = "' deleted";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logConfigError(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc4.getName())){
//                		System.err.println("Error when logging '" + logConfig.getName() + "': " + logConfig.getErrNo());
//                	}                   
//                }
//
//                public void logConfigStarted(LogConfig logConfig) {
//                	if(logConfig.getName().equalsIgnoreCase(lc4.getName())){
//                        String msg = "";
//                        if(logConfig.isStarted()) {
//                            msg = "' started";
//                        } else {
//                            msg = "' stopped";
//                        }
//                        System.out.println("LogConfig '" + logConfig.getName() + msg);
//                	}
//                }
//
//                public void logDataReceived(LogConfig logConfig, Map<String, Number> data, int timestamp) {
//                	if(logConfig.getName().equalsIgnoreCase(lc4.getName())){
//                        System.out.println("timestamp: " + timestamp);
//                        for (Entry<String, Number> entry : data.entrySet()) {
//                            System.out.print("\t" + entry.getKey() + ": " + entry.getValue());
//                        }
//                        System.out.println();
//                	}
//                }
//
//            });
//
//            logg.start(lc4);
            
            // Start a timer to disconnect after 5s
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                    logg.stop(lc);
//                    logg.delete(lc);
//                }
//
//            }, 5000);

            // Start a timer to disconnect after 10s
//            timer.schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                    mCrazyflie.disconnect();
//                }
//
//            }, 10000);
        } else {
            System.err.println("Logg was null!!");
        }
    }

    /*
     * Callback when connection initial connection fails (i.e no Crazyflie at the specified address)
     */
    @Override
    public void connectionFailed(String msg) {
        System.out.println("Connection failed: " + msg);
        setConnected(false);
    }

    /*
     * Callback when disconnected after a connection has been made (i.e. Crazyflie moves out of range)
     */
    @Override
    public void connectionLost(String msg) {
        System.out.println("Connection lost: " + msg);
    }

    /*
     * Callback when the Crazyflie is disconnected (called in all cases)
     */
    @Override
    public void disconnected() {
        System.out.println("Disconnected");
        setConnected(false);
    }

    public static void main(String[] args) {
        // Initialize the low-level drivers (don't list the debug drivers)
        // cflib.crtp.init_drivers(enable_debug_driver=False)

        // Scan for Crazyflies and use the first one found
//        System.out.println("Scanning interfaces for Crazyflies...");
//
//        RadioDriver radioDriver = new RadioDriver(new UsbLinkJava());
//        List<ConnectionData> foundCrazyflies = radioDriver.scanInterface();
//        radioDriver.disconnect();
//
//        System.out.println("Crazyflies found:");
//        for (ConnectionData connectionData : foundCrazyflies) {
//            System.out.println(connectionData);
//        }

        List<ConnectionData> foundCrazyflies = new ArrayList<ConnectionData>();
        foundCrazyflies.add(new ConnectionData(80, Crazyradio.DR_2MPS));

        if (foundCrazyflies.size() > 0) {
            LoggingSensorsExample loggingExample = new LoggingSensorsExample(foundCrazyflies.get(0));

            /**
             * The Crazyflie lib doesn't contain anything to keep the application alive,
             * so this is where your application should do something. In our case we
             * are just waiting until we are disconnected.
             */
            while (loggingExample.isConnected()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("loop is done");
            System.exit(0);
        } else {
            System.out.println("No Crazyflies found, cannot run example");
        }
    }

}
