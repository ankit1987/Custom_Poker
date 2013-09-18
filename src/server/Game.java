package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import player.Player;
import util.GameStates;

/**
 * This game class contains the game state which can be follows
 * 1-PRE_FLOP 
 * 2-FLOP
 * 3-TURN
 * 4-RIVER
 * @author ivy4127
 *
 */
public class Game {
	
GameState currentGameRound;	
	
Player dealer;

Player smallBlind;

Player bigBlind;

int gameNumber;

List<Pot>  sidePots;

Pot currentPot=new Pot();
	
GameStates currentGameState;

Player winner;

List<Player> players=new ArrayList<Player>();

static int sequence=0;

int totalPlayerStartedThisGame=0;



public Game(){
	
	this.gameNumber=++sequence;
	this.currentGameState=GameStates.R1;
	this.currentGameRound=new Round1();
}

public Game(Player dealer, Player smallBlind, Player bigBlind){
	
	this.gameNumber=sequence++;
	this.currentGameState=GameStates.R1;
	this.currentGameRound=new Round1();
	
	this.dealer=dealer;
	this.smallBlind=smallBlind;
	this.bigBlind=bigBlind;

}

public void PlayersAccount(){
	System.out.println("==============================================ACTIVE PLAYER FOR THE GAME===================================================================");
	for(int i=0;i<players.size();i++){
		System.out.println("player::" + players.get(i).getRelativePostionFromDealer() + "amount" + players.get(i).getAmount());
	}
}

void startBetting(){

	PlayersAccount();
	while(!currentGameState.equals(GameStates.FINISH)){
		System.out.println("===========================ROUND::"+ currentGameState+"=================");
		List<Pot> sidePots=currentGameRound.startBettingRound(players, currentPot,gameNumber);
		if(sidePots!=null){
			Pot lastActivePot=sidePots.get(sidePots.size()-1);
			sidePots.remove(sidePots.size()-1);
			sidePots.add(currentPot);
			currentPot=lastActivePot;

		}
		getNextGameState();
	}

}


/**
 * this function decide the winner of this game according to following rule
 * Player with MOD(Current game number, Active player position) = 0
 * 
 */
public void decideWinner(){
	
	List<Integer> possibleWinner=getPossibleWinnerList();
	Collections.sort(possibleWinner);
	
	Collections.reverse(possibleWinner);
	int winner=possibleWinner.get(0);
	this.winner=players.get(winner-1);
	this.winner.setLastGameWinNumber(gameNumber);
	addWinningAmount();
	System.out.println("===========GAME:" + gameNumber + "======================" + "WINNER:" + this.winner.getRelativePostionFromDealer()+ "============================");
}


/**
 * this function check all the pot on which player was playing and add that amount
 */
public void addWinningAmount(){

	int totalWinAmount=0;
	totalWinAmount=getPotMoney(currentPot);

	if(sidePots!=null){
		for (Pot pot:sidePots) {
			totalWinAmount+=getPotMoney(pot);       
		}
	}
	winner.addWinAmount(totalWinAmount);

}


int getPotMoney(Pot pot){
	if(pot.getPlayerVSAmoutContributed().containsKey(winner)){
	Map<Player, Integer> amountMap=pot.getPlayerVSAmoutContributed();
	Set<Player> players=amountMap.keySet();
	int totalWin=0;
	for(Player p:players){
		int amount=amountMap.get(p);
		totalWin+=amount;
	}
	return totalWin;
	}
	return 0;
}


/**
 * this gives all the possible winners list
 * @return
 */
List<Integer> getPossibleWinnerList(){
	List<Integer> winners=new ArrayList<Integer>();

	for(int i=1;i<=players.size();i++){
		if(gameNumber%i==0){
			winners.add(i);
		}
	}
	return winners;
}


/**
 * this function changes the current game state to new game state 
 */
void getNextGameState(){
	switch (currentGameState) {
	case START:
		currentGameState=GameStates.R1;
		break;
	case R1:
		currentGameState=GameStates.R2;
		currentGameRound=new Round2();
		break;
	case R2:
		currentGameState=GameStates.R3;
		currentGameRound=new Round3();
		break;
	case R3:
		currentGameState=GameStates.R4;
		currentGameRound=new Round4();
		break;
	case R4:
		currentGameState=GameStates.FINISH;
		currentGameRound=null;
		break;
	default:
		break;
	}
	

}


public GameStates getCurrentGameState() {
	return currentGameState;
}


public void setCurrentGameState(GameStates currentGameState) {
	this.currentGameState = currentGameState;
}



public Player getDealer() {
	return dealer;
}

public Player getSmallBlind() {
	return smallBlind;
}

public Player getBigBlind() {
	return bigBlind;
}

public int getGameNumber() {
	return gameNumber;
}




public void setDealer(Player dealer) {
	this.dealer = dealer;
}

public void setSmallBlind(Player smallBlind) {
	this.smallBlind = smallBlind;
}

public void setBigBlind(Player bigBlind) {
	this.bigBlind = bigBlind;
}

public List<Player> getPlayers() {
	return players;
}

public void setPlayers(List<Player> players) {
	this.players = players;
}

public void setGameNumber(int gameNumber) {
	this.gameNumber = gameNumber;
}


}
