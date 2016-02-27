package ch.fhnw.oop2.spacegame.math;

public class Vec2 {
	public float x;
	public float y;

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setTo(Vec2 other) {
		setTo(other.x, other.y);
	}

	public void setTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public static Vec2 subtract(Vec2 x, Vec2 y) {
		Vec2 result = new Vec2(x);
		return result.subtract(y);
	}

	public static Vec2 add(Vec2 x, Vec2 y) {
		Vec2 result = new Vec2(x);
		return result.add(y);
	}

	public static Vec2 multiply(Vec2 x, float y) {
		Vec2 result = new Vec2(x);
		return result.multiply(y);
	}

	public float dot(Vec2 other) {
		return x * other.x + y * other.y;
	}

	public Vec2(Vec2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	public Vec2 divide(float scalar) {
		x /= scalar;
		y /= scalar;
		return this;
	}

	public Vec2 add(Vec2 other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public Vec2 subtract(Vec2 other) {
		x -= other.x;
		y -= other.y;
		return this;
	}

	public Vec2 multiply(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Vec2 translate(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static Vec2 norm(Vec2 x) {
		Vec2 result = new Vec2(x);
		return result.normalize();
	}

	public Vec2 normalize() {
		divide(getLength());
		return this;
	}

	public float getLength() {
		return (float) Math.hypot(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		throw new IllegalStateException("Do not call equal for Vec2, implementation could be quite difficult! (would use an epsilon to compare float values).");
	}

	public static float lengthSqr(Vec2 v) {
		return v.x * v.x + v.y * v.y;
	}

	public static float length(Vec2 v) {
		return (float) Math.sqrt(v.x * v.x + v.y * v.y);
	}

	public static float length(float x, float y) {
		return (float) Math.hypot(x, y);
		// return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public static float distance(Vec2 a, Vec2 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return Vec2.length(dx, dy);
	}

}
