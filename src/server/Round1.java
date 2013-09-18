package server;

import util.BetOption;
import util.GameStates;


/**
 * 
 * This class represents the round1 state of the game
 * all the common functionalities has been defined in the super class
 * Round1 doesn't have options like check and bet. 
 *  1.Bet is not there because force bet are used
 *  2. Check option wont be there because SB and BB has to be placed 
 *
 */
public class Round1 extends GameState {

	public Round1(){
		super();
		availableOption.remove(BetOption.CHECK);//check option wont be there in round 1
		availableOption.remove(BetOption.BET);// bet option wont be there in round 1 as forced bet needs to be placed
	}
	
	@Override
	public GameStates getRoundState() {
		return GameStates.R1;
	}

	@Override
	//don't do anything over here. base class will handle the use case
	public void forcedBetBeforeStarting() {
				
	}

}
