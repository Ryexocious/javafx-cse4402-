package com.example.battleshipgame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class BattleshipController implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private GridPane player1Grid;
    @FXML private GridPane player2Grid;
    @FXML private Label currentPlayerLabel;
    @FXML private Label gameStatusLabel;
    @FXML private Button switchModeButton;
    @FXML private VBox shipPlacementPanel;
    @FXML private ComboBox<String> shipSelector;
    @FXML private RadioButton horizontalRadio;
    @FXML private RadioButton verticalRadio;
    @FXML private Button startGameButton;
    @FXML private Button newGameButton;
    @FXML private ListView<String> shipsListView;

    private GameBoard player1Board;
    private GameBoard player2Board;
    private boolean isPlayer1Turn = true;
    private boolean gameStarted = false;
    private boolean placingShipsPhase = true;
    private Ship selectedShip;
    private boolean isDarkMode = false;
    private int currentPlayerShipIndex = 0;
    private boolean player1ShipsPlaced = false;
    private boolean player2ShipsPlaced = false;

    private static final int CELL_SIZE = 40;
    private static final String STYLES_CSS = "/com/example/battleshipgame/styles.css";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGame();
        setupShipPlacementUI();
        updateUI();

        javafx.application.Platform.runLater(() -> {
            if (mainContainer.getScene() != null) {
                mainContainer.getScene().getStylesheets().add(getClass().getResource(STYLES_CSS).toExternalForm());
                applyTheme();
            }
        });
    }

    private void initializeGame() {
        player1Board = new GameBoard();
        player2Board = new GameBoard();

        // Reset all phase variables
        placingShipsPhase = true;
        gameStarted = false;
        isPlayer1Turn = true;
        currentPlayerShipIndex = 0;
        player1ShipsPlaced = false;
        player2ShipsPlaced = false;

        setupGrids();
        setupShipSelector();
        updateGameStatus("Player 1: Place your ships");
    }

    private void setupGrids() {
        setupGrid(player1Grid, true);
        setupGrid(player2Grid, false);
    }

    private void setupGrid(GridPane grid, boolean isPlayer1) {
        grid.getChildren().clear();

        for (int row = 0; row < GameBoard.getGridSize(); row++) {
            for (int col = 0; col < GameBoard.getGridSize(); col++) {
                Rectangle cell = createCell();
                final int r = row, c = col;

                if (placingShipsPhase) {
                    cell.setOnMouseClicked(e -> handleShipPlacement(r, c, isPlayer1));
                } else {
                    cell.setOnMouseClicked(e -> handleShot(r, c, isPlayer1));
                }

                grid.add(cell, col, row);
            }
        }
    }

    private Rectangle createCell() {
        Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
        cell.getStyleClass().add("grid-cell");
        cell.setStroke(Color.BLACK);
        cell.setStrokeWidth(1);
        return cell;
    }

    private void handleShipPlacement(int row, int col, boolean isPlayer1) {
        if (!placingShipsPhase) return;

        GameBoard currentBoard = isPlayer1 ? player1Board : player2Board;

        // Check if it's the correct player's turn for placement
        boolean isCurrentPlayerPlacing = (isPlayer1Turn && isPlayer1) || (!isPlayer1Turn && !isPlayer1);
        if (!isCurrentPlayerPlacing) return;

        if (selectedShip != null) {
            boolean horizontal = horizontalRadio.isSelected();
            if (currentBoard.placeShip(selectedShip, row, col, horizontal)) {
                updateGridDisplay();
                currentPlayerShipIndex++;

                if (currentPlayerShipIndex >= currentBoard.getShips().size()) {
                    // Current player finished placing ships
                    if (isPlayer1Turn) {
                        player1ShipsPlaced = true;
                        isPlayer1Turn = false; // Switch to Player 2
                        currentPlayerShipIndex = 0;
                        updateGameStatus("Player 2: Place your ships");
                        setupShipSelector(); // Setup selector for Player 2
                        setupGrids(); // Reset grids for player 2
                    } else {
                        player2ShipsPlaced = true;
                        placingShipsPhase = false;
                        startGameButton.setDisable(false);
                        shipPlacementPanel.setVisible(false);
                        updateGameStatus("All ships placed! Click 'Start Game' to begin battle.");
                        isPlayer1Turn = true; // Reset to Player 1 for battle phase
                    }
                } else {
                    selectedShip = currentBoard.getShips().get(currentPlayerShipIndex);
                    updateShipSelector();
                }
                updateShipsList();
                updateUI();
            }
        }
    }

    private void handleShot(int row, int col, boolean clickedGridIsPlayer1) {
        if (placingShipsPhase || !gameStarted) return;

        boolean isPlayer1Shooting = isPlayer1Turn;

        // Player 1 shoots only on Player 2's grid (clickedGridIsPlayer1 == false)
        // Player 2 shoots only on Player 1's grid (clickedGridIsPlayer1 == true)
        if ((isPlayer1Shooting && clickedGridIsPlayer1) || (!isPlayer1Shooting && !clickedGridIsPlayer1)) {
            // Clicked own grid - ignore
            return;
        }

        GameBoard targetBoard = isPlayer1Shooting ? player2Board : player1Board;

        if (targetBoard.hasBeenShot(row, col)) {
            updateGameStatus("Already shot here! Try a different spot.");
            return;
        }

        boolean hit = targetBoard.shoot(row, col);
        updateGridDisplay();

        if (hit) {
            Ship hitShip = targetBoard.getShipAt(row, col);
            if (hitShip != null && hitShip.isSunk()) {
                updateGameStatus((isPlayer1Shooting ? "Player 1" : "Player 2") + " sunk a " + hitShip.getType().getName() + "!");

                // Check if all ships are sunk after this hit
                if (targetBoard.allShipsSunk()) {
                    endGame(isPlayer1Shooting ? "Player 1" : "Player 2");
                    return;
                }
            } else {
                updateGameStatus((isPlayer1Shooting ? "Player 1" : "Player 2") + " scored a hit!");
            }

            // Continue turn on hit (optional - uncomment next line to switch turn on hit)
            // isPlayer1Turn = !isPlayer1Turn;
        } else {
            updateGameStatus((isPlayer1Shooting ? "Player 1" : "Player 2") + " missed!");
            isPlayer1Turn = !isPlayer1Turn; // Switch turn on miss
        }

        currentPlayerLabel.setText("Current Player: " + (isPlayer1Turn ? "Player 1" : "Player 2"));
    }

    private void updateGridDisplay() {
        updateGridDisplay(player1Grid, player1Board, true);
        updateGridDisplay(player2Grid, player2Board, false);
    }

    private void updateGridDisplay(GridPane grid, GameBoard board, boolean isPlayer1Grid) {
        for (int row = 0; row < GameBoard.getGridSize(); row++) {
            for (int col = 0; col < GameBoard.getGridSize(); col++) {
                Rectangle cell = (Rectangle) grid.getChildren().get(row * GameBoard.getGridSize() + col);
                GameBoard.CellState state = board.getCellState(row, col);

                cell.getStyleClass().removeAll("ship-cell", "hit-cell", "miss-cell", "empty-cell");

                if (placingShipsPhase) {
                    // Show ships during placement phase for current player only
                    boolean showShips = (isPlayer1Grid && isPlayer1Turn && !player1ShipsPlaced) ||
                            (!isPlayer1Grid && !isPlayer1Turn && !player2ShipsPlaced) ||
                            (isPlayer1Grid && player1ShipsPlaced) ||
                            (!isPlayer1Grid && player2ShipsPlaced);

                    if (state == GameBoard.CellState.SHIP && showShips) {
                        cell.getStyleClass().add("ship-cell");
                    } else if (state == GameBoard.CellState.HIT) {
                        cell.getStyleClass().add("hit-cell");
                    } else if (state == GameBoard.CellState.MISS) {
                        cell.getStyleClass().add("miss-cell");
                    } else {
                        cell.getStyleClass().add("empty-cell");
                    }
                } else {
                    // Battle phase: hide ships; only show hits/misses
                    if (state == GameBoard.CellState.HIT) {
                        cell.getStyleClass().add("hit-cell");
                    } else if (state == GameBoard.CellState.MISS) {
                        cell.getStyleClass().add("miss-cell");
                    } else {
                        cell.getStyleClass().add("empty-cell");
                    }
                }
            }
        }
    }

    private void setupShipPlacementUI() {
        ToggleGroup orientationGroup = new ToggleGroup();
        horizontalRadio.setToggleGroup(orientationGroup);
        verticalRadio.setToggleGroup(orientationGroup);
        horizontalRadio.setSelected(true);
    }

    private void setupShipSelector() {
        if (shipSelector.getItems().isEmpty()) {
            shipSelector.getItems().addAll("Battleship (3)", "Destroyer (2)", "Destroyer (2)",
                    "Submarine (1)", "Submarine (1)", "Submarine (1)");
        }

        GameBoard currentBoard = isPlayer1Turn ? player1Board : player2Board;
        if (currentPlayerShipIndex < currentBoard.getShips().size()) {
            selectedShip = currentBoard.getShips().get(currentPlayerShipIndex);
            shipSelector.getSelectionModel().select(currentPlayerShipIndex);
        }
    }

    private void updateShipSelector() {
        if (currentPlayerShipIndex < shipSelector.getItems().size()) {
            shipSelector.getSelectionModel().select(currentPlayerShipIndex);
        }
    }

    private void updateShipsList() {
        shipsListView.getItems().clear();
        GameBoard currentBoard = isPlayer1Turn ? player1Board : player2Board;

        for (int i = 0; i < currentBoard.getShips().size(); i++) {
            Ship ship = currentBoard.getShips().get(i);
            String status = i < currentPlayerShipIndex ? "✓ Placed" : i == currentPlayerShipIndex ? "→ Current" : "Waiting";
            shipsListView.getItems().add(ship.getType().getName() + " (" + ship.getSize() + ") - " + status);
        }
    }

    private void endGame(String winner) {
        gameStarted = false;
        placingShipsPhase = false;

        updateGameStatus("Game Over! " + winner + " wins!");

        newGameButton.setDisable(false);
        startGameButton.setDisable(true);

        disableGrids();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(winner + " wins the game!");
        alert.showAndWait();
    }

    private void disableGrids() {
        removeGridHandlers(player1Grid);
        removeGridHandlers(player2Grid);
    }

    private void removeGridHandlers(GridPane grid) {
        for (var node : grid.getChildren()) {
            node.setOnMouseClicked(null);
        }
    }

    private void updateGameStatus(String status) {
        gameStatusLabel.setText(status);
    }

    private void updateUI() {
        currentPlayerLabel.setText("Current Player: " + (isPlayer1Turn ? "Player 1" : "Player 2"));
        updateShipsList();
        updateGridDisplay();
    }

    @FXML
    private void startGame() {
        placingShipsPhase = false;
        gameStarted = true;
        isPlayer1Turn = true;
        startGameButton.setDisable(true);
        newGameButton.setDisable(false);
        shipPlacementPanel.setVisible(false);
        updateGameStatus("Battle begins! Player 1's turn to shoot.");

        setupGrids();
        updateGridDisplay();
        updateUI();
    }

    @FXML
    private void newGame() {
        initializeGame();

        startGameButton.setDisable(true);
        newGameButton.setDisable(true);
        shipPlacementPanel.setVisible(true);

        updateUI();
    }

    @FXML
    private void switchMode() {
        isDarkMode = !isDarkMode;
        applyTheme();
        switchModeButton.setText(isDarkMode ? "Light Mode" : "Dark Mode");
    }

    private void applyTheme() {
        if (mainContainer.getScene() == null) {
            return;
        }
        mainContainer.getStyleClass().removeAll("light-theme", "dark-theme");
        if (isDarkMode) {
            mainContainer.getStyleClass().add("dark-theme");
        } else {
            mainContainer.getStyleClass().add("light-theme");
        }
    }
}