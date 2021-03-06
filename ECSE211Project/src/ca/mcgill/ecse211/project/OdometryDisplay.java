package ca.mcgill.ecse211.project;

/**
 * Odometry Display class
 * @author Antonios Valkanas, Borui Tao
 * @version 1.0
 * 
 */

import lejos.hardware.lcd.TextLCD;

/**
 * Displays the values that the odometer updates.
 */
public class OdometryDisplay extends Thread {
	private static final long DISPLAY_PERIOD = 250;
	private Odometer odometer;
	private TextLCD t;
	private UltrasonicLocalizer ul;

	/**
	 * The constructor for the odometry display that sets the odometer, the text lcd and the US localizer
	 * @param odometer		pointer to the Odometer
	 * @param t				pointer to the LCT text
	 * @param ul			pointer to the UltrasonicLocalizer
	 */
	public OdometryDisplay(Odometer odometer, TextLCD t, UltrasonicLocalizer ul) {
		this.odometer = odometer;
		this.t = t;
		this.ul = ul;
	}

	/**
	 * The method runs in a thread and keeps updating the robot's screen
	 */
	public void run() {
		long displayStart, displayEnd;
		double[] position = new double[3];
		t.clear();

		while (true) {
			displayStart = System.currentTimeMillis();

			// clear the lines for displaying odometry information
			t.drawString("X:              ", 0, 0);
			t.drawString("Y:              ", 0, 1);
			t.drawString("T:              ", 0, 2);
		    t.drawString("US Distance: " + ul.readUSDistance(), 0, 3); // print last US reading
			// get the odometry information
			odometer.getPosition(position, new boolean[] {true, true, true});

			// display odometry information
			for (int i = 0; i < 3; i++) {
				t.drawString(formattedDoubleToString(position[i], 2), 3, i);
			}

			// throttle the OdometryDisplay
			displayEnd = System.currentTimeMillis();
			if (displayEnd - displayStart < DISPLAY_PERIOD) {
				try {
					Thread.sleep(DISPLAY_PERIOD - (displayEnd - displayStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that OdometryDisplay will be interrupted
					// by another thread
				}
			}
		}
	}

	/**
	 * The methods formats an number (double) into a String.
	 * @param x			the number to format
	 * @param places	the number of decimals to add 
	 * @return			the String of the number.
	 */
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}

			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long) x);
			}
		}

		return result;
	}

}