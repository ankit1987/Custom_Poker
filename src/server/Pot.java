package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import player.Player;


public class Pot {

	


//total amount
int amount;


Map<Player, Integer> playerVSAmoutContributed=new HashMap<Player, Integer>();

List<Player> allInPlayers=new ArrayList<Player>();

public Map<Player, Integer> getPlayerVSAmoutContributed() {
	return playerVSAmoutContributed;
}


public List<Player> getAllInPlayers() {
	return allInPlayers;
}


public void setPlayerVSAmoutContributed(Map<Player, Integer> playerVSAmoutContributed) {
	this.playerVSAmoutContributed = playerVSAmoutContributed;
}



int getCurrentStakeForPlayer(Player player){
	
	return playerVSAmoutContributed.get(player);
}

public void setAllInPlayers(List<Player> allInPlayers) {
	this.allInPlayers = allInPlayers;
}


public Pot(){
	amount=0;
}


public

void addMoney(int amount){
	this.amount+=amount;
}

public int getAmount() {
	return amount;
}


public void setAmount(int amount) {
	this.amount = amount;
}

public void addAllInPlayer(Player player){
	allInPlayers.add(player);
}


}
