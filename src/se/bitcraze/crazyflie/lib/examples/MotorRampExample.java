package se.bitcraze.crazyflie.lib.examples;
import se.bitcraze.crazyflie.lib.crazyflie.ConnectionAdapter;
import se.bitcraze.crazyflie.lib.crazyflie.Crazyflie;
import se.bitcraze.crazyflie.lib.crazyradio.ConnectionData;
import se.bitcraze.crazyflie.lib.crazyradio.Crazyradio;
import se.bitcraze.crazyflie.lib.crazyradio.RadioDriver;
import se.bitcraze.crazyflie.lib.crtp.CommanderPacket;
import se.bitcraze.crazyflie.lib.usb.UsbLinkJava;

/**
 *  Simple example that connects to the first Crazyflie found,
 *  ramps up/down the motors and disconnects.
 *
 */
public class MotorRampExample {

    private Crazyflie mCrazyflie;
    /**
     * Initialize and run the example with the specified link_uri
     */
    public MotorRampExample(ConnectionData connectionData) {

        mCrazyflie = new Crazyflie(new RadioDriver(new UsbLinkJava()));

        mCrazyflie.getDriver().addConnectionListener(new ConnectionAdapter() {

            /**
             * This callback is called from the Crazyflie API when a Crazyflie
             + has been connected and the TOCs have been downloaded.
             */
            public void connected() {
                System.out.println("CONNECTED");

                // Start a separate thread to do the motor test.
                // Do not hijack the calling thread!
//                Thread(target=self._ramp_motors).start()               
            }
            
            @Override
            public void setupFinished() {
                System.out.println("SETUP FINISHED");
                try {
                	rampMotors();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*
             * Callback when the Crazyflie is disconnected (called in all cases)
             */
            public void disconnected() {
                System.out.println("DISCONNECTED");
            }

            /*
             * Callback when connection initial connection fails (i.e no Crazyflie at the specified address)
             */
            public void connectionFailed(String msg) {
                System.out.println("CONNECTION FAILED - Msg: " + msg);
            }

            /**
             * Callback when disconnected after a connection has been made (i.e Crazyflie moves out of range)
             *
             * @param connectionInfo
             */
            public void connectionLost(String connectionInfo) {
                System.out.println("CONNECTION LOST: " +  connectionInfo);
            }

        });

        mCrazyflie.setConnectionData(connectionData);
        mCrazyflie.connect();

        System.out.println("Connection to " + connectionData);
    }

    public void rampMotors() {
        int thrust_mult = 1;
        int thrust_step = 500;
        long thrust = 20000;
        float pitch = 0;
        float roll = 0;
        float yawrate = 0;

        // send packet with zero thrust to arm the copter
        this.mCrazyflie.sendPacket(new CommanderPacket(0, 0, 0, (char) 0));
        while (thrust >= 20000) {
        	this.mCrazyflie.sendPacket(new CommanderPacket(roll, pitch, yawrate, (char) thrust));
            System.out.println("sendPacket: " + thrust);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (thrust >= 25000) {
                thrust_mult = -1;
            }
            thrust += thrust_step * thrust_mult;
        }
        this.mCrazyflie.sendPacket(new CommanderPacket(0, 0, 0, (char) 0));
        // Make sure that the last packet leaves before the link is closed
        // since the message queue is not flushed before closing
        // time.sleep(0.1)
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mCrazyflie.disconnect();
    }

    public static void main(String[] args) {
        // Initialize the low-level drivers (don't list the debug drivers)
//        cflib.crtp.init_drivers(enable_debug_driver=False)
        // Scan for Crazyflies and use the first one found
        System.out.println("Scanning interfaces for Crazyflies...");
//        available = cflib.crtp.scan_interfaces()
        System.out.println("Crazyflies found:");
        /*
        for i in available:
            print i[0]
        if len(available) > 0:
            le = MotorRampExample(available[0][0])
        else:
            print "No Crazyflies found, cannot run example"
        */
        new MotorRampExample(new ConnectionData(80, Crazyradio.DR_2MPS));
    }

}