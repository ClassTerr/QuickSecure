package QuickSecure;

import javafx.animation.Interpolator;

/**
 * Animation interpolator that used to move images in encoding window
 */
public class FastAnimationInterpolator extends Interpolator {
	@Override
	protected double curve(double t) {
		return Math.pow(t, 0.5);
	}
}