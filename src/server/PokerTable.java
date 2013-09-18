package server;

import java.util.ArrayList;
import java.util.List;

import player.Player;
import util.Configuration;

/**
 * 
 * This class is responsible to handle the active player list and start the game
 * 
 * Player position is getting initialized first time
 * Game play will start if at least 3 active player are on the table(dealer, small blind, big blind)
 * @author Ankit Tyagi
 *
 */
public class PokerTable {


List<Game> games=new ArrayList<Game>();

Game currentGame;

List<Player> players;


public void initializeTable(){
	initializePlayer();
}

/**
 * this function initialize the player
 */
public void initializePlayer(){
   players=new ArrayList<Player>();
	for(int i=1;i<=Configuration.TOTAL_PLAYER;i++){
		Player player=new Player(i, Configuration.playerVSMoney.get(i));

		players.add(player);
	}

}



public void startNewGame() {
	while(true){
		currentGame = new Game();

		for (Player player : players) {
			   player.reinitialize();
				if (player.getAmount() > 0) {
					player.setActive(true);
				}else{
				    player.setSitting(false);
				}
			}
		// get dealer for this current games
		Player dealer=getDealerForCurrentGame();
		currentGame.setDealer(dealer);
		dealer.setDealer(true);

		//get the small blind and set to currentGame
		Player smallBlind= getSmallBlindForCurrentGame(dealer);
		currentGame.setSmallBlind(smallBlind);
		smallBlind.setSmallBlind(true);

		//get the big blind and set to currentGame
		Player bigBlind= getBigBlindForCurrentGame(smallBlind);
		currentGame.setBigBlind(bigBlind);
		bigBlind.setBigBlind(true);

		setRelativePosition(dealer);
		
		List<Player> activePlayers=prepareActivePlayerListForCurrentGame(smallBlind);

		if(activePlayers.size()<3){//for a game atleast 3 players are required 1.Dealer 2.Small Blind 3.Big Blind
			break;
		}
		currentGame.setPlayers(activePlayers);
		currentGame.startBetting();
		currentGame.decideWinner();
		games.add(currentGame);
		try {
			System.out.println("<======================================NEXT GAME WILL START AFTER 5 SEC===============================================================>");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



	/**
	 * this function returns the dealer for current game
	 * @return
	 */
	Player getDealerForCurrentGame(){
		Player dealer=null;
		if(games.size()==0){
			dealer= players.get(Configuration.TOTAL_PLAYER-1);
		}else{
			Game game=games.get(games.size()-1);
			int preDealer=game.getDealer().getPosition();
			int dealerPosition= getNextPosition(preDealer,players.size());
			dealer=players.get(dealerPosition-1);
		}
		
		while(!dealer.isActive()){
			int nextPosition=getNextPosition(dealer.getPosition(),players.size());
			dealer=players.get(nextPosition-1);
		}		
		
		return dealer;
	}
	
   /**
    * this function returns the small Blind for current game
    * @param dealer
    * @return
    */
	Player getSmallBlindForCurrentGame(Player dealer){

		int smallBlindPosition=getNextPosition(dealer.getPosition(),players.size());
		Player smallBlind=players.get(smallBlindPosition-1);

		while(!smallBlind.isActive()){
			int nextPosition=getNextPosition(smallBlind.getPosition(), players.size());
			smallBlind=players.get(nextPosition-1);
		}
		return smallBlind;	 
	}

	

	 /**
	  * this function returns the big blind for current game
	  * @param smallBlind
	  * @return
	  */
	 Player getBigBlindForCurrentGame(Player smallBlind){
		 
			int bigBlindPosition=getNextPosition(smallBlind.getPosition(), players.size());
			Player bigBlind=players.get(bigBlindPosition-1);

			while(!bigBlind.isActive()){
				int nextPosition=getNextPosition(bigBlind.getPosition(), players.size());
				bigBlind=players.get(nextPosition-1);
			}
			return bigBlind;	 
	 
	 }	
	

	 int getNextPosition(int currentPosition, int totalSize){
		
		int nextPosition=  ((currentPosition + 1) ==totalSize) ? (currentPosition + 1):(currentPosition + 1)%totalSize;
		return nextPosition;
	}
	




/**
 * this function returns the active players list for this round
 * @param smallBlind
 * @return
 */
public List<Player> prepareActivePlayerListForCurrentGame(Player smallBlind){
	
	List<Player> activePlayers=new ArrayList<Player>();
	int position=smallBlind.getPosition();
	for(int i=0;i<players.size();i++){
		if(players.get(position-1).isActive()){
		activePlayers.add(players.get(position-1));
		}
		position=getNextPosition(position, players.size());
		
		
	}
	return activePlayers;
}


/**
 * this function calculates the relative position of all playes relative to dealer in current game
 * @param dealer
 */
public void setRelativePosition(Player dealer){

	for(Player p:players){
		int relativePosition= (p.getPosition()-dealer.getPosition())%players.size();
		if(relativePosition<0){
			relativePosition=players.size() + relativePosition;
		}
		p.setRelativePostionFromDealer(relativePosition);
	}


}

 
 public static void main(String[] args) {

	 PokerTable table=new PokerTable();
	 table.initializeTable();
	 table.startNewGame();

 }
 
}
