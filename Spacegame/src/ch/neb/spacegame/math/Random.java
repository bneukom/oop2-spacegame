package ch.neb.spacegame.math;

public class Random {
	@SafeVarargs
	public static <T> T selectRandom(T... objects) {
		if (objects.length == 0)
			return null;
		return objects[(int) (Math.random() * (objects.length))];
	}
}
