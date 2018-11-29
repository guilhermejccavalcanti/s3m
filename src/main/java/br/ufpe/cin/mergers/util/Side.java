package br.ufpe.cin.mergers.util;

public enum Side {
    LEFT, RIGHT;

    public Side opposite() {
        if (this == LEFT) return RIGHT;
        if (this == RIGHT) return LEFT;

        return null;
    }
}
