package com.example.battleshipgame;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    public enum CellState {
        EMPTY, SHIP, HIT, MISS
    }

    private static final int GRID_SIZE = 9;
    private CellState[][] grid;
    private List<Ship> ships;
    private List<Ship.Position> shots;

    public GameBoard() {
        grid = new CellState[GRID_SIZE][GRID_SIZE];
        ships = new ArrayList<>();
        shots = new ArrayList<>();
        initializeGrid();
        initializeShips();
    }

    private void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = CellState.EMPTY;
            }
        }
    }

    private void initializeShips() {
        ships.add(new Ship(Ship.ShipType.BATTLESHIP));
        ships.add(new Ship(Ship.ShipType.DESTROYER));
        ships.add(new Ship(Ship.ShipType.DESTROYER));
        ships.add(new Ship(Ship.ShipType.SUBMARINE));
        ships.add(new Ship(Ship.ShipType.SUBMARINE));
        ships.add(new Ship(Ship.ShipType.SUBMARINE));
    }

    public boolean placeShip(Ship ship, int startRow, int startCol, boolean horizontal) {
        if (canPlaceShip(ship, startRow, startCol, horizontal)) {
            ship.setPositions(startRow, startCol, horizontal);
            updateGridWithShip(ship);
            return true;
        }
        return false;
    }

    private boolean canPlaceShip(Ship ship, int startRow, int startCol, boolean horizontal) {
        int size = ship.getSize();

        // Check bounds
        if (horizontal && startCol + size > GRID_SIZE) return false;
        if (!horizontal && startRow + size > GRID_SIZE) return false;
        if (startRow < 0 || startCol < 0) return false;

        // Check for overlapping ships
        for (int i = 0; i < size; i++) {
            int row = horizontal ? startRow : startRow + i;
            int col = horizontal ? startCol + i : startCol;

            if (grid[row][col] == CellState.SHIP) {
                return false;
            }
        }

        return true;
    }

    private void updateGridWithShip(Ship ship) {
        for (Ship.Position pos : ship.getPositions()) {
            grid[pos.getRow()][pos.getCol()] = CellState.SHIP;
        }
    }

    public boolean shoot(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return false;
        }

        Ship.Position shotPos = new Ship.Position(row, col);
        if (shots.contains(shotPos)) {
            System.out.println("Already shot at " + shotPos);
            return false; // Already shot here
        }

        shots.add(shotPos);

        // Check if shot hits any ship
        for (Ship ship : ships) {
            if (ship.hit(shotPos)) {
                grid[row][col] = CellState.HIT;
                System.out.println("Hit ship: " + ship.getType().getName() + " at " + row + "," + col);

                if (ship.isSunk()) {
                    System.out.println(ship.getType().getName() + " is sunk!");
                }

                return true;
            }
        }

        grid[row][col] = CellState.MISS;
        System.out.println("Missed at " + shotPos);
        return false;
    }

    public boolean moveShip(Ship ship, int newRow, int newCol) {
        if (ship.canMoveTo(newRow, newCol, GRID_SIZE)) {
            // Clear old positions from grid
            for (Ship.Position pos : ship.getPositions()) {
                if (!ship.getHitPositions().contains(pos)) {
                    grid[pos.getRow()][pos.getCol()] = CellState.EMPTY;
                }
            }

            // Move ship
            ship.moveTo(newRow, newCol);

            // Update grid with new positions
            updateGridWithShip(ship);
            return true;
        }
        return false;
    }

    public boolean allShipsSunk() {
        boolean allSunk = ships.stream().allMatch(Ship::isSunk);
        if (allSunk) {
            System.out.println("All ships are sunk!");
        }
        return allSunk;
    }

    public Ship getShipAt(int row, int col) {
        Ship.Position pos = new Ship.Position(row, col);
        for (Ship ship : ships) {
            if (ship.getPositions().contains(pos)) {
                return ship;
            }
        }
        return null;
    }

    // Getters
    public static int getGridSize() { return GRID_SIZE; }
    public CellState getCellState(int row, int col) { return grid[row][col]; }
    public List<Ship> getShips() { return new ArrayList<>(ships); }
    public List<Ship.Position> getShots() { return new ArrayList<>(shots); }
    public boolean hasBeenShot(int row, int col) {
        return shots.contains(new Ship.Position(row, col));
    }
}
