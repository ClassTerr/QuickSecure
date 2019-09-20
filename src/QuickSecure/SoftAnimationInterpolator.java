package QuickSecure;

import javafx.animation.Interpolator;

/**
 * Class that represents interpolator with sinus movement
 * 
 * @author ClassTerr
 *
 */
public class SoftAnimationInterpolator extends Interpolator {
	@Override
	protected double curve(double t) {
		return Math.sin(t * 2 * Math.PI) + 1;
	}
}