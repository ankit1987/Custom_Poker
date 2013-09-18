package server;

import util.GameStates;

/**
 * this class represents the Round3 state of game
 * @author ivy4127
 *
 */
public class Round3 extends GameState{
	
	public Round3(){
		super();
	}

	@Override
	public GameStates getRoundState() {
		return GameStates.R3;	
		
	}

}
