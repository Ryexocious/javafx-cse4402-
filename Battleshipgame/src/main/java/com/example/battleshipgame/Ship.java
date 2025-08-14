package com.example.battleshipgame;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    public enum ShipType {
        BATTLESHIP(3, "Battleship"),
        DESTROYER(2, "Destroyer"),
        SUBMARINE(1, "Submarine");

        private final int size;
        private final String name;

        ShipType(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public int getSize() { return size; }
        public String getName() { return name; }
    }

    private ShipType type;
    private List<Position> positions;
    private boolean isHorizontal;
    private boolean isDamaged;
    private List<Position> hitPositions;

    public Ship(ShipType type) {
        this.type = type;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
        this.isHorizontal = true;
        this.isDamaged = false;
    }

    public void setPositions(int startRow, int startCol, boolean horizontal) {
        positions.clear();
        this.isHorizontal = horizontal;

        for (int i = 0; i < type.getSize(); i++) {
            if (horizontal) {
                positions.add(new Position(startRow, startCol + i));
            } else {
                positions.add(new Position(startRow + i, startCol));
            }
        }
    }

    public boolean hit(Position pos) {
        if (positions.contains(pos) && !hitPositions.contains(pos)) {
            hitPositions.add(pos);
            isDamaged = true;
            System.out.println("Ship " + type.getName() + " got hit at " + pos);
            return true;
        }
        return false;
    }

    public boolean isSunk() {
        boolean sunk = hitPositions.size() == type.getSize();
        if (sunk) {
            System.out.println(type.getName() + " ship is sunk!");
        }
        return sunk;
    }

    public boolean canMoveTo(int newRow, int newCol, int gridSize) {
        if (!isDamaged || isSunk()) return false;

        for (int i = 0; i < type.getSize(); i++) {
            int row = isHorizontal ? newRow : newRow + i;
            int col = isHorizontal ? newCol + i : newCol;

            if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
                return false;
            }
        }
        return true;
    }

    public void moveTo(int newRow, int newCol) {
        setPositions(newRow, newCol, isHorizontal);
    }

    // Getters
    public ShipType getType() { return type; }
    public List<Position> getPositions() { return new ArrayList<>(positions); }
    public List<Position> getHitPositions() { return new ArrayList<>(hitPositions); }
    public boolean isHorizontal() { return isHorizontal; }
    public boolean isDamaged() { return isDamaged; }
    public int getSize() { return type.getSize(); }

    public static class Position {
        private int row, col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() { return row; }
        public int getCol() { return col; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Position position = (Position) obj;
            return row == position.row && col == position.col;
        }

        @Override
        public int hashCode() {
            int result = Integer.hashCode(row);
            result = 31 * result + Integer.hashCode(col);
            return result;
        }

        @Override
        public String toString() {
            return "(" + row + "," + col + ")";
        }
    }
}
