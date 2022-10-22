package edu.hm.dako.common;

import org.junit.jupiter.api.Test;

class TupelTest {

    @Test
    void setX() {
        Tupel<Integer, String> tupel = new Tupel<>(42, "Max");
        tupel.setX(21);
        assert (tupel.getX() == 21);
    }

    @Test
    void setY() {
        Tupel<String[], Boolean> tupel = new Tupel<>(new String[]{"A", "B"}, true);
        tupel.setY(false);
        assert !tupel.getY();
    }

    @Test
    void getX() {
        Tupel<Double, Float> tupel = new Tupel<>(1.2, 2.785f);
        assert ((tupel.getX() - 1.2) < 0.005);
    }

    @Test
    void getY() {
        Tupel<Character, Long> tupel = new Tupel<>('a', 42L);
        assert (tupel.getY() == 42L);
    }
}