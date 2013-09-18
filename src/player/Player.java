package player;

import java.util.List;
import java.util.Random;

import server.GameState;
import util.BetOption;
import util.GameStates;



public class Player implements IPlayer {



// this variable tells about the player position on poker table
int position;

//this variable tell about the player postion relative with dealer(this gets changed in every game)
int relativePostionFromDealer;

// this variable tells abount current money; 
int amount;

// number of wins so far(game count)
int numberOfWins;

// number of loses so far(game count)
int numberOfLoses;

//this variable is to identify whether user is still active on table
boolean isActive;

//this variable is used to identify whether user is still on the table or he has folded
boolean isSitting;

int lastGameWinNumber;


BetOption lastTakenAction;

//true:if current player is dealer, false:otherwise  
boolean isDealer;

//true: if current player is smallBlind 
boolean isSmallBlind;

//true:if current player is bigblind
boolean isBigBlind;

boolean hasFolded;

boolean hasAllInded;

int lastActiveBetAmount;



public Player(int position, int amount){
	
	this.position=position;
	this.amount=amount;
	this.isSitting=true;
	this.isActive=false;
	this.isDealer=false;
	this.isSmallBlind=false;
	this.isBigBlind=false;
	this.hasFolded=false;
	this.hasAllInded=false;
	this.lastActiveBetAmount=0;
}


/**
 * this function is used to reinitialize the state of player before starting new game
 */
public void reinitialize(){
	this.isSitting=true;
	this.isActive=false;
	this.isDealer=false;
	this.isSmallBlind=false;
	this.isBigBlind=false;
	this.hasFolded=false;
	this.hasAllInded=false;
	this.lastActiveBetAmount=0;
	
}


/**
 * Player choose the betOption from the available option according to following rules
 *  1. Check is always preferred over fold
 *	2. Only one raise is allowed in a round. For eg if P1 has raised in a round then all other active players will
 *      either call or fold and no player will raise in that particular round
 *	3. Games since last win:
 *	    a. Player after winning a game will not raise for next 5 games
 *	    b. Player will go all-in in the 6th game after winning a game
 *	4. Dealer will always be there in the game – so he will always call if there is any raise or check in all the
 *      rounds except Round 2 where he will definitely raise
 *	5. Position: Every 3rd player will always be in the game like the dealer
 *	6. Big Blind can only check or fold in R1 and R3
 *	7. Position: Every odd position will call or check in R3 & R4 and will only check or fold in R1 & R2
 *	8. Big blind will go all-in in R4
 *	9. Every 5th game 3rd player will raise in R1
 *	10. Every 7th game Small Blind will go all-in in R1
 * @throws InterruptedException 
 */
public BetOption chooseAvailableOption(List<BetOption> betOptions, GameStates state, boolean isRaiseHappened, boolean isBetPlaced,int currentGameNumber, int lastBetAmount) {

	System.out.println("\nchoose available option::" + betOptions);

	restrictedBetOption(betOptions, state, isRaiseHappened, currentGameNumber);

	if(lastGameWinNumber!=0 && currentGameNumber-this.lastGameWinNumber== 6){//player has to go all-in in 6th game after winning a game
		lastTakenAction=BetOption.ALL_IN;
	}else if(this.isDealer && state.equals(GameStates.R2)){
		lastTakenAction=BetOption.RAISE;
	}else if(currentGameNumber%5==0 && getRelativePostionFromDealer()==3 && state.equals(GameStates.R1)){
		lastTakenAction=BetOption.RAISE;
	}
	else if (isDealer() || getRelativePostionFromDealer()==3){
		if(isBetPlaced || isRaiseHappened){
			lastTakenAction=BetOption.CALL;
		}else{
			lastTakenAction=BetOption.BET;
		}
	}else if(isBigBlind() && state.equals(GameStates.R3)){
		if(betOptions.contains(BetOption.CHECK)){
			lastTakenAction=BetOption.CHECK;
		}else{
			lastTakenAction=BetOption.FOLD;
		}
	}
	else if(getRelativePostionFromDealer()%2!=0 && state.equals(GameStates.R2)){
		lastTakenAction=getBetOptionForOddPositionInInitialRounds(betOptions);
	}else if (getRelativePostionFromDealer()%2!=0 && (state.equals(GameStates.R3) || state.equals(GameStates.R4))){
		getBetOptionForOddPositionInLastRounds(betOptions);
	}else if (this.isBigBlind && state.equals(GameStates.R4)){// if this is bigBlind and game is in R4 state
		System.out.println("i am round 4 and callig raise-in");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastTakenAction=BetOption.ALL_IN;
	}else if (currentGameNumber%7==0 && isSmallBlind()){
		lastTakenAction=BetOption.ALL_IN;
	}else{
		lastTakenAction=getRandomOption(betOptions, lastBetAmount);
	}
	return lastTakenAction;
}


public void restrictedBetOption(List<BetOption> betOptions, GameStates state, boolean isRaiseHappened, int currentGameNumber){

	if(isRaiseHappened){// if someone raised in perticular round then no other player will raise in that perticular round
		betOptions.remove(BetOption.RAISE);
	}else if(lastGameWinNumber!=0 && currentGameNumber-this.lastGameWinNumber<= 5){//player can't raise for next 5 five games
		betOptions.remove(BetOption.RAISE);
	}else if (!this.isDealer && state.equals(GameStates.R2)){
		betOptions.remove(BetOption.RAISE);
	}else if (currentGameNumber%5==0 && getRelativePostionFromDealer()!=3 && state.equals((GameStates.R1))){
		betOptions.remove(BetOption.RAISE);
	}

}



BetOption getBetOptionForOddPositionInInitialRounds(List<BetOption> betOptions){
	
	if(betOptions.contains(BetOption.CHECK)){
		return BetOption.CHECK;
	}else if (betOptions.contains(BetOption.FOLD)){
		return BetOption.FOLD;
	}else{
		System.out.println("something wrong:::::not expected:: getBetOptionForOddPositionInInitialRounds()" + "betoptionss" + betOptions);
		return null;
	}
}


BetOption getBetOptionForOddPositionInLastRounds(List<BetOption> betOptions){

	if(betOptions.contains(BetOption.CHECK)){
		return BetOption.CHECK;
	}else if (betOptions.contains(BetOption.CALL)){
		return BetOption.CALL;
	}else{
		System.out.println("something wrong:::::not expected:: getBetOptionForOddPositionInLastRounds" + betOptions);
		return null;
	}
}


/**
 * this function returns the random option from the list of option 
 * 1- check is always preffered over fold
 * 2- if bet didn't happen in this round then choose BET instead call or raise

 * @param betOptions
 * @return
 */
BetOption getRandomOption(List<BetOption> betOptions, int lastBetAmount){
	if(betOptions.contains(BetOption.ALL_IN)){
		betOptions.remove(BetOption.ALL_IN);
	}

	Random random=new Random();

	int randomIndex=random.nextInt(betOptions.size());
	BetOption randomOption= betOptions.get(randomIndex);
	if(randomOption.equals(BetOption.FOLD)){
		if(betOptions.contains(BetOption.CHECK)){
			randomOption=BetOption.CHECK;
		}else{// decreasing the probability of fold option
			randomIndex=random.nextInt(betOptions.size());
			randomOption= betOptions.get(randomIndex);
		}

		return randomOption;
	}else if (lastBetAmount==0){
		System.out.println("==========LAST BET AMOUNT IS ZERO===========" + lastBetAmount);
		return BetOption.BET;
	}
	else if (getAmount() < lastBetAmount){
		return BetOption.ALL_IN;
	}else{
		return randomOption;
	}
}


public void addWinAmount(int amount){
	this.amount=this.amount+amount;
}


@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + position;
	return result;
}



@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Player other = (Player) obj;
	if (position != other.position)
		return false;
	return true;
}



public int reduceStake(int stake){
	lastActiveBetAmount=stake;
	this.amount= amount-stake;
	return stake;
}



public int getPosition() {
	return position;
}


public int getAmount() {
	return amount;
}

public int getNumberOfWins() {
	return numberOfWins;
}


public int getNumberOfLoses() {
	return numberOfLoses;
}


public boolean isActive() {
	return isActive;
}

public BetOption getLastTakenAction() {
	return lastTakenAction;
}

public void setPosition(int position) {
	this.position = position;
}


public void setAmount(int amount) {
	this.amount = amount;
}



public void setNumberOfWins(int numberOfWins) {
	this.numberOfWins = numberOfWins;
}


public void setNumberOfLoses(int numberOfLoses) {
	this.numberOfLoses = numberOfLoses;
}



public void setActive(boolean isActive) {
	this.isActive = isActive;
}


public void setLastTakenAction(BetOption lastTakenAction) {
	this.lastTakenAction = lastTakenAction;
}

public boolean isSitting() {
	return isSitting;
}


public void setSitting(boolean isSitting) {
	this.isSitting = isSitting;
}



public int getLastGameWinNumber() {
	return lastGameWinNumber;
}


public boolean isDealer() {
	return isDealer;
}


public boolean isSmallBlind() {
	return isSmallBlind;
}


public boolean isBigBlind() {
	return isBigBlind;
}


public void setLastGameWinNumber(int lastGameWinNumber) {
	this.lastGameWinNumber = lastGameWinNumber;
}


public void setDealer(boolean isDealer) {
	this.isDealer = isDealer;
}


public void setSmallBlind(boolean isSmallBlind) {
	this.isSmallBlind = isSmallBlind;
}

public int getRelativePostionFromDealer() {
	return relativePostionFromDealer;
}



public void setRelativePostionFromDealer(int relativePostionFromDealer) {
	this.relativePostionFromDealer = relativePostionFromDealer;
}



public void setBigBlind(boolean isBigBlind) {
	this.isBigBlind = isBigBlind;
}



public int getLastActiveBetAmount() {
	return lastActiveBetAmount;
}



public void setLastActiveBetAmount(int lastActiveBetAmount) {
	this.lastActiveBetAmount = lastActiveBetAmount;
}



@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("PLAYER:Position").append(getRelativePostionFromDealer());
		return sb.toString();
	}
}
