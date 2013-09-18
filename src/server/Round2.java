package server;

import util.GameStates;

/**
 * this class represents the Round2 state of Game
 * @author ivy4127
 *
 */
public class Round2 extends GameState{

	
	public Round2(){
		super();
	}
	
	@Override
	public GameStates getRoundState() {
		return GameStates.R2;
	}



}
