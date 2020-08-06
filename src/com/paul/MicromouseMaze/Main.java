package com.paul.MicromouseMaze;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("app_layout.fxml"));
		GridPane rootGridPane = loader.load();
		//rootGridPane.setGridLinesVisible(true);
		controller = loader.getController();
		controller.createPlayground();



		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		//rootGridPane.getChildren().add(0,menuBar);

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().addAll(menuBar);

		Scene scene = new Scene(rootGridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Micromouse Maze Solver");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private MenuBar createMenu() {
		Menu fileMenu =new Menu("File");
		MenuItem newMenuItem = new MenuItem("New Game");

		newMenuItem.setOnAction(event -> {
			resetGame();

		});

		MenuItem resetMenuItem = new MenuItem("Reset Game");

		resetMenuItem.setOnAction(event -> {
			resetGame();
		});

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitMenuItem = new MenuItem("Exit Game");

		exitMenuItem.setOnAction(event -> {
			quitGame();
		});

		fileMenu.getItems().addAll(newMenuItem,resetMenuItem,separatorMenuItem,exitMenuItem);


		Menu helpMenu =new Menu("Help");
		MenuItem aboutGame = new MenuItem("About Game");

		aboutGame.setOnAction(event -> {
			aboutGame();
		});

		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();

		MenuItem aboutMe = new MenuItem("About Me");

		aboutMe.setOnAction(event -> aboutMe());

		helpMenu.getItems().addAll(aboutGame,separatorMenuItem1,aboutMe);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu,helpMenu);

		return menuBar;
	}

	private void resetGame() {
		controller.clearMaze();
	}

	private void quitGame() {
		Platform.exit();
		System.exit(0);
	}

	private void aboutMe() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the developer");
		alert.setHeaderText("Baishalee Paul");
		alert.setContentText("I am a beginner in JavaFX and wish to become a pro in developing Java applications.");
		alert.show();
	}

	private void aboutGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the App");
		alert.setHeaderText("Micromouse Maze solver");
		alert.setContentText("This application allows the user to create a maze, set start and goal positions and " +
				"the program finds the shortest path from the start to the goal.");
		alert.show();
	}
}
