package ch.neb.spacegame.math;

public class Random {
	@SafeVarargs
	public static <T> T selectRandom(T... objects) {
		if (objects.length == 0)
			return null;
		return objects[(int) (Math.random() * (objects.length))];
	}

	public static float randomSignum() {
		return Math.random() > 0.5 ? -1 : 1;
	}
}
