package edu.hm.dako.common;

/**
 * combines two different data types to one unit
 *
 * @param <X> data type 1
 * @param <Y> data type 2
 */
public class Tupel <X, Y> {
    /**
     * generic first type
     */
    private X x;

    /**
     * generic second type
     */
    private Y y;

    /**
     * Konstruktor
     *
     * @param x value 1
     * @param y value 2
     */
    public Tupel(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * setter
     *
     * @param x value 1
     */
    public void setX(X x) {
        this.x = x;
    }

    /**
     * setter
     *
     * @param y value 2
     */
    public void setY(Y y) {
        this.y = y;
    }

    /**
     * getter
     *
     * @return x: value 1
     */
    public X getX() {
        return x;
    }

    /**
     * getter
     *
     * @return y: value 2
     */
    public Y getY() {
        return y;
    }
}