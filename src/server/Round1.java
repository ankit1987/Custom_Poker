package server;

import util.BetOption;
import util.GameStates;


/**
 * 
 * @author ivy4127
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
