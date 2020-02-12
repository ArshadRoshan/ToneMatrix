package com.example.tonematrix;

import java.util.Timer;
import java.util.TimerTask;
import com.example.tonematrix.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

// S6whVpuQ
public class ToneMatrix extends Activity implements OnClickListener {

	public boolean grid[][];
	public Button controlButton, clearButton;
	public Button buttonGrid[][];

	private int timeStep = 120;

	int gridLength = 8;
	int gridHeight = 8;

	int counter;
	private Timer sequencer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// load up sounds
		SoundManager.getInstance();
		SoundManager.initSounds(this);
		SoundManager.loadSounds();

		counter = 0;

		// initialise grid to store on/off states
		grid = new boolean[gridHeight][gridLength];

		// initialise array of Button objects
		buttonGrid = new Button[gridHeight][gridLength];

		// get pointers to UI objects
		controlButton = (Button)findViewById(R.id.control_button);
		clearButton = (Button)findViewById(R.id.clear_button);

		Drawable controlDrawable = this.getResources().getDrawable(R.drawable.control);
		controlButton.setBackgroundDrawable(controlDrawable);
		clearButton.setBackgroundDrawable(controlDrawable);

		// handler for buttons
		Button.OnClickListener onClickListener = (new Button.OnClickListener(){ 

			public void onClick(View v) {

				Button checkButton = (Button) v;

				// control start/stop actions
				if(checkButton == controlButton){
					if(sequencer == null){
						startSeq();
					} else{
						stopSeq();
					}	

					// or clear the grid	
				}else if(checkButton == clearButton){
					initialiseGrid();
				}

			}

		});

		// add listeners
		controlButton.setOnClickListener(onClickListener);
		clearButton.setOnClickListener(onClickListener);

		TableLayout sequencerTableHolder = (TableLayout)findViewById(R.id.table_holder);
		sequencerTableHolder.setPadding(1,1,1,1);

		// initialise grid of buttons
		for (int column = 0; column < gridLength; column++) {
			TableRow tempRow = new TableRow(this);
			for (int rowElement = 0; rowElement < gridHeight; rowElement++) {
				buttonGrid[column][rowElement] = new Button (this);
				buttonGrid[column][rowElement].setOnClickListener(this);
				tempRow.addView(buttonGrid[column][rowElement], 35,35);
			}
			sequencerTableHolder.addView(tempRow);
		}

		// make sure grid is cleared
		initialiseGrid();
	} 

	// function to wipe the board clean
	private void initialiseGrid(){

		Drawable unselectedDrawable = this.getResources().getDrawable(R.drawable.unselected);

		for (int column = 0; column < gridLength; column++) {
			for (int rowElement = 0; rowElement < gridHeight; rowElement++) {
				buttonGrid[column][rowElement].setBackgroundDrawable(unselectedDrawable);
				grid[column][rowElement] = false;
			}
		}
	}

	// start running the sequencer
	public void startSeq(){

		// use a timer to trigger the next step of the sequencer
		sequencer = new Timer();
		sequencer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				playSequencerLine();
			}
		}, 10, timeStep);
		controlButton.setText("Pause");

	}

	public void stopSeq(){
		if(sequencer != null){
			sequencer.cancel();
			sequencer = null;
			controlButton.setText("Start");
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		stopSeq();
	}

	public void playSequencerLine(){

		counter++;
		if(counter > 7) counter = 0;
		
		for(int i = 0; i < gridHeight; i++){
			if(grid[i][counter]){
				SoundManager.playSound(i+1, 1);
			}
		}
	}


	// handler for button grid
	public void onClick(View view) {

		Button buttonCheck = (Button) view;

		int posX = -1, posY = -1;
		for (int i = 0; i < buttonGrid[0].length; i++){
			for (int j = 0; j < buttonGrid.length; j++){
				if (buttonGrid[i][j] == buttonCheck){
					posX = i;
					posY = j;
					break;
				}
			}
		}

		Drawable myImage;

		// flip state
		grid[posX][posY] = !grid[posX][posY];

		if(grid[posX][posY]) myImage = this.getResources().getDrawable(R.drawable.selected);
		else myImage = this.getResources().getDrawable(R.drawable.unselected);

		// and update image
		buttonGrid[posX][posY].setBackgroundDrawable(myImage);
	}


}