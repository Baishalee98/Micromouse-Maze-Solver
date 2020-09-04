package com.paul.MicromouseMaze;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class Controller implements Initializable {

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane mazePane;

	@FXML
	public Button setBlocksButton;

	@FXML
	public Button setStartButton;

	@FXML
	public Button setGoalButton;

	@FXML
	public Button findPathButton;

	@FXML
	public Button resetMazeButton;

	private static final int ROWS = 8;
	private static final int COLUMNS = 8;
	private static final int SMALLERSQUARELENGTH = 80;
	private static final String COLOR_BLOCK = "#000000";
	private static final String COLOR_START = "#00ff00";
	private static final String COLOR_GOAL = "#ff0000";
	private static final String COLOR_PATH = "#ffff00";
	private static boolean flagBlock = false;
	private static boolean flagStart = false;
	private static boolean flagGoal = false;
	private static int StartXIndex = -1;
	private static int StartYIndex = -1;
	private static int GoalXIndex = -1;
	private static int GoalYIndex = -1;
	private static boolean StartPresent = false;
	private static boolean GoalPresent = false;
	private static int StartKey = -1;
	private static int GoalKey = 0;
	private static int BlockKey = -2;
	private static String prevString = "";
	private static String direction = "up";
	private static List<Integer> path = new ArrayList<Integer>();
	private static boolean PathSet = false;

	Cell[][] clickableSquares;


	//for finding path using BFS
	Queue<Integer> queue = new LinkedList<>();




	public void createPlayground(){
		Shape outerRectangle = createGameStructuralGrid();
		rootGridPane.add(outerRectangle,0,1);

		clickableSquares= createClickableCells();

		for (int row = 0; row < ROWS; row++){
			for (int col = 0; col < COLUMNS; col++){
				rootGridPane.add(clickableSquares[row][col],0,1);
			}
		}

		setBlocksButton.setOnAction(event -> {
			if (PathSet){
				alertPathSet();
			}else {
				flagBlock = true;
				flagStart = false;
				flagGoal = false;
			}
		});

		setStartButton.setOnAction(event -> {
			if (PathSet){
				alertPathSet();
			}else {
				flagStart = true;
				flagBlock = false;
				flagGoal = false;
			}
		});

		setGoalButton.setOnAction(event -> {
			if (PathSet){
				alertPathSet();
			}else {
				flagGoal = true;
				flagBlock = false;
				flagStart = false;
			}
		});

		findPathButton.setOnAction(event -> {
			if (!PathSet) {

				if (flagBlock) flagBlock = false;
				if (StartPresent && GoalPresent) {
					queue.add(GoalXIndex);
					queue.add(GoalYIndex);
					findKeys();
					findPath(StartXIndex, StartYIndex, "", path);
					drawPath();
					PathSet = true;
				} else if (!StartPresent) {
					alertStartNotPresent();
				} else if (!GoalPresent) {
					alertGoalNotPresent();
				}

				clearKeys();
			}else {
				alertPathSet();
			}
		});

		resetMazeButton.setOnAction(event -> {
			clearPath();
			clearKeys();
		});

	}

	public void clearPath() {
		for (int i=2;i<path.size()-2;i+=2){
			clickableSquares[path.get(i)][path.get(i+1)].setFill(Color.TRANSPARENT);
		}
		path.clear();
		PathSet = false;
	}

	private void alertPathSet() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error occurred");
		alert.setHeaderText("Path already set");
		alert.setContentText("Click \"Reset Maze\" to alter maze");
		alert.show();
	}

	private void drawPath() {
		for (int i=2;i<path.size()-2;i+=2){
			clickableSquares[path.get(i)][path.get(i+1)].setFill(Paint.valueOf(COLOR_PATH));
		}
	}


	//Finds path after allotting keys to cells
	private void findPath(int robx, int roby, String s, List<Integer> path) {
		path.add(robx);
		path.add(roby);

		if(clickableSquares[robx][roby].key==0)return;
		switch(direction){
			case "up":

				if(robx>0 && roby>=0 && clickableSquares[robx-1][roby].key!=-2 && clickableSquares[robx-1][roby].key<clickableSquares[robx][roby].key){
					robx--;
				}
				else  if(robx>=0 && roby>0 && clickableSquares[robx][roby-1].key!=-2 && clickableSquares[robx][roby-1].key<clickableSquares[robx][roby].key){
					roby--;direction="left";
				}
				else  if(robx<ROWS && roby<COLUMNS-1 && clickableSquares[robx][roby+1].key!=-2 && clickableSquares[robx][roby+1].key<clickableSquares[robx][roby].key){

					roby++;direction="right";

				}
				else if(robx<ROWS-1 && roby<COLUMNS && clickableSquares[robx+1][roby].key!=-2 && clickableSquares[robx+1][roby].key<clickableSquares[robx][roby].key){
					robx++;direction="down";
				}
				break;
			case "left":
				if(robx>=0 && roby>0 && clickableSquares[robx][roby-1].key!=-2 && clickableSquares[robx][roby-1].key<clickableSquares[robx][roby].key){

					roby--;

				}
				else if(robx>0 && roby>=0 && clickableSquares[robx-1][roby].key!=-2 && clickableSquares[robx-1][roby].key<clickableSquares[robx][roby].key){

					robx--;direction="up";
				}
				else  if(robx<ROWS-1 && roby<COLUMNS && clickableSquares[robx+1][roby].key!=-2 && clickableSquares[robx+1][roby].key<clickableSquares[robx][roby].key){

					robx++;direction="down";
				}
				else if(robx<ROWS && roby<COLUMNS-1 && clickableSquares[robx][roby+1].key!=-2 && clickableSquares[robx][roby+1].key<clickableSquares[robx][roby].key){

					roby++;direction="right";

				}
				break;
			case "right":
				if(robx<ROWS && roby<COLUMNS-1 && clickableSquares[robx][roby+1].key!=-2 && clickableSquares[robx][roby+1].key<clickableSquares[robx][roby].key){

					roby++;

				}
				else if(robx>0 && roby>=0 && clickableSquares[robx-1][roby].key!=-2 && clickableSquares[robx-1][roby].key<clickableSquares[robx][roby].key){

					robx--;direction="up";
				}
				else if(robx<ROWS-1 && roby<COLUMNS && clickableSquares[robx+1][roby].key!=-2 && clickableSquares[robx+1][roby].key<clickableSquares[robx][roby].key){

					robx++;direction="down";
				}
				else  if(robx>=0 && roby>0 && clickableSquares[robx][roby-1].key!=-2 && clickableSquares[robx][roby-1].key<clickableSquares[robx][roby].key){

					roby--;direction="left";

				}
				break;
			case "down":
				if(robx<ROWS-1 && roby<COLUMNS && clickableSquares[robx+1][roby].key!=-2 && clickableSquares[robx+1][roby].key<clickableSquares[robx][roby].key){

					robx++;
				}
				else  if(robx>=0 && roby>0 &&  clickableSquares[robx][roby-1].key!=-2 && clickableSquares[robx][roby-1].key<clickableSquares[robx][roby].key){

					roby--;direction="left";

				}
				else  if(robx<ROWS && roby<COLUMNS-1 &&  clickableSquares[robx][roby+1].key!=-2 && clickableSquares[robx][roby+1].key<clickableSquares[robx][roby].key){

					roby++;direction="right";

				}
				else if(robx>0 && roby>=0 && clickableSquares[robx-1][roby].key!=-2 && clickableSquares[robx-1][roby].key<clickableSquares[robx][roby].key){

					robx--;direction="top";
				}
				break;
		}
		findPath(robx, roby, direction,path);
	}

	public void clearKeys() {
		for (int row=0;row<ROWS;row++){
			for (int col=0;col<COLUMNS;col++){
				Cell cell = clickableSquares[row][col];
				if(cell.key != 0 && cell.key != -2)
					cell.key = -1;
			}
		}
	}

	public void clearMaze(){
		if (StartPresent){
			Cell cell = clickableSquares[StartXIndex][StartYIndex];
			cell.setFill(Color.TRANSPARENT);
			cell.key = -1;
			StartXIndex = -1;
			StartYIndex = -1;
			StartPresent = false;
		}
		if (GoalPresent){
			Cell cell = clickableSquares[GoalXIndex][GoalYIndex];
			cell.setFill(Color.TRANSPARENT);
			cell.key = -1;
			GoalXIndex = -1;
			GoalYIndex = -1;
			GoalPresent = false;
		}
		for (int row=0;row<ROWS;row++){
			for (int col=0;col<COLUMNS;col++){
				Cell cell = clickableSquares[row][col];
				if(cell.key != -1) {
					cell.key = -1;
					cell.setFill(Color.TRANSPARENT);
				}
			}
		}


		//new modification
		clearPath();
		clearKeys();

	}

	/*
	private void findKeys(int x,int y) {
		if (clickableSquares[x][y].key == -2)return;
		if(x>0 && y>=0){
			if(clickableSquares[x-1][y].key == -1 || clickableSquares[x-1][y].key > clickableSquares[x][y].key+1){

				clickableSquares[x-1][y].key = clickableSquares[x][y].key+1;

				findKeys(x-1, y);
			}
		}
		if(x>=0 && y>0){
			if(clickableSquares[x][y-1].key==-1 || clickableSquares[x][y-1].key>clickableSquares[x][y].key+1){
				clickableSquares[x][y-1].key=clickableSquares[x][y].key+1;
				findKeys(x, y-1);
			}
		}
		if(x<ROWS-1 && y<COLUMNS){
			if(clickableSquares[x+1][y].key==-1 || clickableSquares[x+1][y].key>clickableSquares[x][y].key+1){
				clickableSquares[x+1][y].key=clickableSquares[x][y].key+1;
				findKeys(x+1, y);
			}
		}
		if(x<ROWS && y<COLUMNS-1){
			if(clickableSquares[x][y+1].key==-1 || clickableSquares[x][y+1].key>clickableSquares[x][y].key+1){
				clickableSquares[x][y+1].key=clickableSquares[x][y].key+1;
				findKeys(x, y+1);
			}
		}
	}

	 */


	//Allotting keys to cells using BFS
	private void findKeys() {
		while(!queue.isEmpty()){
			int x=queue.poll();
			int y=queue.poll();

			if(x>0 && y>=0){
				if(clickableSquares[x-1][y].key == -1 || clickableSquares[x-1][y].key > clickableSquares[x][y].key+1){

					clickableSquares[x-1][y].key = clickableSquares[x][y].key+1;

					queue.add(x-1);
					queue.add(y);
				}
			}

			if(x>=0 && y>0){
				if(clickableSquares[x][y-1].key==-1 || clickableSquares[x][y-1].key>clickableSquares[x][y].key+1){
					clickableSquares[x][y-1].key=clickableSquares[x][y].key+1;

					queue.add(x);
					queue.add(y-1);
				}
			}
			if(x<ROWS-1 && y<COLUMNS){
				if(clickableSquares[x+1][y].key==-1 || clickableSquares[x+1][y].key>clickableSquares[x][y].key+1){
					clickableSquares[x+1][y].key=clickableSquares[x][y].key+1;

					queue.add(x+1);
					queue.add(y);
				}
			}
			if(x<ROWS && y<COLUMNS-1){
				if(clickableSquares[x][y+1].key==-1 || clickableSquares[x][y+1].key>clickableSquares[x][y].key+1){
					clickableSquares[x][y+1].key=clickableSquares[x][y].key+1;

					queue.add(x);
					queue.add(y+1);
				}
			}

		}

	}




	//If goal not set, show alert
	private void alertGoalNotPresent() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error occurred");
		alert.setHeaderText("Goal not set");
		alert.setContentText("Set the Goal position and try to run again");
		alert.show();
	}

	//If start not set, show alert
	private void alertStartNotPresent() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error occurred");
		alert.setHeaderText("Start not set");
		alert.setContentText("Set the Start position and try to run again");
		alert.show();
	}



	private Cell[][] createClickableCells() {
		Cell[][] clickableCells= new Cell[ROWS][COLUMNS];

		for (int row = 0; row < ROWS; row++){
			for (int col = 0; col < COLUMNS; col++){
				Cell rectangle = new Cell(row,col);
				final int r=row,c=col;
				rectangle.setOnMouseClicked(event -> {
					if (flagBlock){
						setBlock(r,c);

					}else if (flagStart && rectangle.key != -2){
						setStart(r,c);
						flagStart = false;

					}else if (flagGoal && rectangle.key != -2){
						setGoal(r,c);
						flagGoal = false;
					}

				});

				clickableCells[row][col] = (Cell) rectangle;
			}
		}
		return clickableCells;
	}

	private void setGoal(int row, int col) {
		Cell cell = clickableSquares[row][col];
		if (StartXIndex == row && StartYIndex == col){
			deleteStart();
		}
		if (GoalPresent)deleteGoal();
		cell.key = 0;
		cell.setFill(Paint.valueOf(COLOR_GOAL));
		GoalXIndex = row;
		GoalYIndex = col;
		GoalPresent = true;
	}

	private void setBlock(int row,int col) {
		Cell cell = clickableSquares[row][col];
		if (cell.key == 0){
			deleteGoal();
		}
		if (StartXIndex == row && StartYIndex == col){
			deleteStart();
		}
		cell.key = -2;
		cell.setFill(Paint.valueOf(COLOR_BLOCK));
	}


	private void setStart(int row,int col) {
		Cell cell = clickableSquares[row][col];
		if (cell.key == 0){
			deleteGoal();
		}
		if (StartPresent)deleteStart();
		cell.key = -1;
		cell.setFill(Paint.valueOf(COLOR_START));
		StartXIndex = row;
		StartYIndex = col;
		StartPresent = true;
	}

	private void deleteStart() {
		Cell cell = clickableSquares[StartXIndex][StartYIndex];
		cell.key = -1;
		cell.setFill(Color.TRANSPARENT);
		StartXIndex = -1;
		StartYIndex = -1;
		StartPresent = false;
	}

	private Shape createGameStructuralGrid() {
		Shape outerSquare = new Rectangle(ROWS * SMALLERSQUARELENGTH, COLUMNS * SMALLERSQUARELENGTH);

		for (int row = 0; row < ROWS; row++){
			for (int col = 0; col < COLUMNS; col++){
				Shape innerSquare = new Rectangle(SMALLERSQUARELENGTH-2,SMALLERSQUARELENGTH-2);
				innerSquare.setTranslateX(col * SMALLERSQUARELENGTH);
				innerSquare.setTranslateY(row * SMALLERSQUARELENGTH);
				outerSquare = Shape.subtract(outerSquare,innerSquare);
			}
		}

		return outerSquare;
	}

	private class Cell extends Rectangle{
		private int key;

		public Cell(int row,int col){
			super(SMALLERSQUARELENGTH-2,SMALLERSQUARELENGTH-2);
			setTranslateX(col * SMALLERSQUARELENGTH);
			setTranslateY((row-3.5) * SMALLERSQUARELENGTH);
			setSmooth(true);
			this.key = -1;
			setFill(Color.TRANSPARENT);
			/*setOnMouseEntered(event -> {
				if (this.key == -1) {
					setFill(Paint.valueOf("66B2FF50"));
				}
			});
			setOnMouseExited(event -> {
				if(this.key == -1)
					setFill(Color.TRANSPARENT);
			});*/

		}
	}

	private void deleteGoal() {
		Cell cell = clickableSquares[GoalXIndex][GoalYIndex];
		cell.key = -1;
		cell.setFill(Color.TRANSPARENT);
		GoalPresent = false;
		GoalXIndex = -1;
		GoalYIndex = -1;
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
