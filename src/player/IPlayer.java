package player;

import java.util.List;

import util.BetOption;
import util.GameStates;

public interface IPlayer {

	
	public BetOption chooseAvailableOption(List<BetOption> betOptions, GameStates state, boolean isRaiseHappened, boolean isBetPlaced,int currentGameNumber, int lastBetAmount);
}
