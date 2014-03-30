package com.kabunny.app;

public class Vector2 {
    public float x;
    public float y;

    /**
     * Construct a new vector
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(float c) {
        this(c, c);
    }

    public Vector2(Vector2 v) {
        this(v.x, v.y);
    }

    public Vector2() {
        this(0, 0);
    }

    /**
     * Make a copy of this vector
     */
    @Override
    public Vector2 clone() {
        return new Vector2(x, y);
    }

    /**
     * Convert to string
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Addition (modifies this vector)
     */
    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(float c) {
        return add(c, c);
    }

    public Vector2 add(Vector2 v) {
        return add(v.x, v.y);
    }

    /**
     * Subtraction (modifies this vector)
     */
    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 sub(float c) {
        return sub(c, c);
    }

    public Vector2 sub(Vector2 v) {
        return sub(v.x, v.y);
    }

    /**
     * Scale (modifies this vector)
     */
    public Vector2 scl(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 scl(float c) {
        return scl(c, c);
    }

    public Vector2 scl(Vector2 v) {
        return scl(v.x, v.y);
    }

    /**
     * Comparison for equality
     */
    public boolean equals(float x, float y) {
        return (this.x == x) && (this.y == y);
    }

    public boolean equals(float c) {
        return equals(c, c);
    }

    public boolean equals(Vector2 v) {
        return equals(v.x, v.y);
    }
}
