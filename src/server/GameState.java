package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import player.Player;
import util.BetOption;
import util.Configuration;
import util.GameStates;

public abstract class GameState {

	private boolean isRoundComplete = false;

	protected boolean isSmallBlindBetDone = false;

	protected boolean isBigBlindBetDone = false;

	Player playerStartedThisRound = null;
	
	Player playerPlacedTheBet=null;

	boolean isRaiseHappened=false;
	
	boolean isBetPlaced=false;

	Player playerWhoRaisedInthisRound = null;

	protected List<BetOption> availableOption = null;

	int lastStake = 0;
	
	public GameState(){
	availableOption= new ArrayList<BetOption>();
	availableOption.addAll(BetOption. allBetOptions);

	}
	
    /**
     * this function is used for start betting in this round
     * after completion betting, if any player do all-in then it create the side pot and returns the list to Game
     * @param players
     * @param pot
     * @return
     */
	public List<Pot> startBettingRound(List<Player> players, Pot pot,int gameNumber) {
		forcedBetBeforeStarting();
		while (!isRoundComplete) {
			for (Player player : players) {
				if(player.isActive() && player.isSitting()){
					if (player.isSmallBlind() && !isSmallBlindBetDone) {// smallBlind  put forced bet first time
						lastStake = player.reduceStake(Configuration.SMALL_BLIND);
						addMoneyToPot(player, lastStake, pot);
						isSmallBlindBetDone = true;
											
					} else if (player.isBigBlind() && !isBigBlindBetDone) {// bigBlind put forced bet first time
						lastStake = player.reduceStake(Configuration.BIG_BLIND);
						addMoneyToPot(player, lastStake, pot);
						isBigBlindBetDone = true;
						isBetPlaced=true;
						playerPlacedTheBet=player;
					} else {
						if (checkForRoundCompletion(player,players)) {// if current round has been completed
							isRoundComplete = true;
							break;
						} else {
							if (playerStartedThisRound == null) {// mark that player who actually started this round
								playerStartedThisRound = player;
							}


							BetOption option = player.chooseAvailableOption(getCopyOfAvailableOption(availableOption), getRoundState(), isRaiseHappened, isBetPlaced,gameNumber, lastStake);
							doProcessingAfterBettingOption(option, player, pot);
						}
					}
				}
			}
		}
		return createSidePotForAllInPlayers(pot);		
	}

	
	
	/**
	 * this function checks whether particular round has been completed. Round gets completed in following question
	 * 1. if only one player active player on the table
	 * 2- call comes to that person who raised last
	 * 3- call comes to that person who started the round
	 * @param player
	 * @return
	 */
	public boolean checkForRoundCompletion(Player player,List<Player> players) {

		if(getTotalActivePlayerInThisRound(players)==1){
			return true;
		}else if ( isRaiseHappened && player == playerWhoRaisedInthisRound) {
			return true;
		}else if (!isRaiseHappened && isBetPlaced && player==playerPlacedTheBet){
			return true;
		}else {
			if (!isRaiseHappened && playerStartedThisRound != null && player == playerStartedThisRound) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param players
	 * @return
	 */
	int getTotalActivePlayerInThisRound(List<Player> players){
		int count=0;
		for(Player player: players){
			if(player.isActive() && player.isSitting()){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * this function creates the copy of available option
	 * @param betOptions
	 * @return
	 */
	List<BetOption> getCopyOfAvailableOption(List<BetOption> betOptions){

		List<BetOption> copyOptions=new ArrayList<BetOption>();
		for(int i=0;i<betOptions.size();i++){

			copyOptions.add(betOptions.get(i));

		}
		return copyOptions;
	}
	
	/**
	 * this function create the subPot in case of all-in
	 * {@link}http://www.learn-texas-holdem.com/questions/when-a-side-pot-is-created.htm
	 * @param pot
	 * @return
	 */
	public List<Pot> createSidePotForAllInPlayers(Pot pot) {

		List<Player> players = pot.getAllInPlayers();
		List<Pot> sidePots=null;

		if (players.size() > 0) {
			sidePots = new ArrayList<Pot>();


			for (int i = 0; i < players.size(); i++) {
				Pot sidePot = createSidePot(pot, players);
				sidePots.add(sidePot);
				pot = sidePot;
			}

		}
		return sidePots;
	}

	/**
	 * this function create the side pot
	 * @param pot
	 * @param players
	 * @return
	 */
	public Pot createSidePot(Pot pot, List<Player> players) {
		
		System.out.println("all-ind player::" + players + "pot map::" + pot.getPlayerVSAmoutContributed()) ;

		Player sortest = null;
		int amount = -1;
		for (Player player : players) {
			if (sortest == null) {
				sortest = player;
				amount = pot.getPlayerVSAmoutContributed().get(player);
			} else {
				int money = pot.getPlayerVSAmoutContributed().get(player);
				if (money < amount) {
					sortest = player;
					amount = money;
				}
			}
		}
		players.remove(sortest);
		return createSubPot(sortest, pot, amount);
	}

	/**
	 * this function create the subPot
	 * 
	 * @param player
	 * @param mainPot
	 * @param amountAllIn
	 * @return
	 */
	public Pot createSubPot(Player player, Pot mainPot, int amountAllIn) {

		Pot subPot = new Pot();

		Set<Player> mainPotPlayer = mainPot.getPlayerVSAmoutContributed().keySet();

		for (Player p : mainPotPlayer) {

			int moneyStake = mainPot.getCurrentStakeForPlayer(p);

			if (moneyStake > amountAllIn) {
				mainPot.getPlayerVSAmoutContributed().put(p, amountAllIn);
				subPot.getPlayerVSAmoutContributed().put(p,
						moneyStake - amountAllIn);
			}

		}
		return subPot;
	}



	/**
	 * this method does the processing after getting option from player
	 * 1- if player choose fold, then make him disabled for subsequent round
	 * 2- if play choose raise, call, bet then reduce that amount from his balance and add that money to pot
	 * 3- if player choose all-in option then make him disabled with subsequent round
	 *    i) if his amount more than last stake then change the last stake for subsequent player
	 *    ii) if his amount less than last stake then no need to change last stake
	 * @param option
	 * @param player
	 * @param pot
	 */
	public void doProcessingAfterBettingOption(BetOption option, Player player,Pot pot) {
		switch (option) {
		case CHECK:
			System.out.println("player choose CHECK option:: pot value:: " + pot.getAmount()+ "\tplayer position::" + player.getRelativePostionFromDealer() + "\tround::" + getRoundState());
			break;

		case BET:
			doProcessingForBetOption(player, pot);	
			break;

		case FOLD:
			doProcessingForFoldOption(player);
			break;

		case CALL:
			doProcessingForCallOption(player, pot);
			break;

		case RAISE:
			doProcessingForRaiseOption(player, pot);
			break;
	
		case ALL_IN:
			doProcessingForAllinOption(player, pot);
			break;
		
		default:
			break;
		}
	}

	/**
	 * this function do the processing for player in case of fold option
	 * 
	 * @param player
	 */
	public void doProcessingForFoldOption(Player player){
		player.setActive(false);
		System.out.println("player choose FOLD option:: making him disabled for the next successive rounds" + "\tplayer position::" + player.getPosition());
	}
	
	public void doProcessingForBetOption(Player player,Pot pot){
		int amount=getBetAmount(player);
		lastStake=amount;
		isBetPlaced=true;
		playerPlacedTheBet=player;
		addMoneyToPot(player, amount, pot);
		System.out.println("player choose BET option:: for amount::" + amount + "\t player position::"+ player.getRelativePostionFromDealer()  +"\tpot amount::" + pot.getAmount()+ "\tround::" + getRoundState()) ;
		// after opting bet oprtion, player can't choose check and bet option in that round
		availableOption.remove(BetOption.CHECK);
		availableOption.remove(BetOption.BET);

	}



	public void doProcessingForCallOption( Player player,Pot pot){
		if(lastStake> player.getAmount()){ // if last stake more than use amount then FOLD for this hand
			doProcessingForAllinOption(player, pot);
		}else{
			int alreadyPutMoney=0;
			if(pot.playerVSAmoutContributed.containsKey(player)){
				
				alreadyPutMoney=pot.playerVSAmoutContributed.get(player);
			//System.out.println("===ALREADY MONEY IN CALL OPTION=====" + alreadyPutMoney) ;
			} 
			int remainingMoney=lastStake-alreadyPutMoney;
			addMoneyToPot(player, player.reduceStake(remainingMoney), pot);
			System.out.println("player choose CALL option:: matching the last stake::" + lastStake +"\tplayer position::" + player.getRelativePostionFromDealer() + "\tpot amount::" + pot.getAmount() + "\tround::" + getRoundState());
		}
	}

	public void doProcessingForRaiseOption(Player player,Pot pot){
		int raiseAmount=getMultipleOfSmallBlindForRaise(pot);
		if(lastStake + raiseAmount > player.getAmount()){
			doProcessingForAllinOption(player, pot);
		}else{
			lastStake = player.reduceStake(lastStake+raiseAmount);
			addMoneyToPot(player, lastStake, pot);
			isRaiseHappened=true;
			playerWhoRaisedInthisRound=player;
			System.out.println("player choose raise option:: increasing the pot amount" + lastStake + "\tplayer position::" + player.getRelativePostionFromDealer()+ "\tplayer amount::" + player.getAmount() + "\tpot amount::" +pot.getAmount()+ "\tround::" + getRoundState()) ;
		}
	}


	public void doProcessingForAllinOption(Player player,Pot pot){
		if (player.getAmount() > lastStake) {
			lastStake = player.reduceStake(player.getAmount());
			isRaiseHappened=true;
			playerWhoRaisedInthisRound=player;
		}
		addMoneyToPot(player,  player.reduceStake(player.getAmount()), pot);
		player.setSitting(false);
		pot.addAllInPlayer(player);
		System.out.println("player choose all-in  option:: increasing the pot amount" + lastStake +"\tplayer position::" + player.getRelativePostionFromDealer()+  "\tplayer amount::" + player.getAmount() + "pot value::" + pot.getAmount()) ;
	}

   /**
    * this function returns the bet amount in case of bet option
    * @param player
    * @return
    */
	public int getBetAmount(Player player){
		if(player.getAmount() > Configuration.BIG_BLIND){
			return Configuration.BIG_BLIND;
		}else if(player.getAmount()> Configuration.SMALL_BLIND){
			return Configuration.SMALL_BLIND;
		}else{
			return player.getAmount();
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param amount
	 * @param pot
	 */
	void addMoneyToPot(Player player, int amount, Pot pot) {
		Map<Player, Integer> p = pot.getPlayerVSAmoutContributed();
		if (p.containsKey(player)) {
			int extingamount = p.get(player);
			p.put(player, extingamount + amount);

		} else {
			p.put(player, amount);
		}

		pot.addMoney(amount);
		System.out.println("ADDING MONEY TO POT" + pot.getPlayerVSAmoutContributed());
	}

	/**
	 * this function returns the Raise amount will be equal to (Pot value/2)
	 * rounded up to a multiple of small blind.
	 * 
	 * @param pot
	 * @return
	 */
	int getMultipleOfSmallBlindForRaise(Pot pot) {
		int halfPot = pot.getAmount() / 2;
		int reminder = halfPot % Configuration.SMALL_BLIND;
		int amount= halfPot - reminder;
		return amount;
	}

	public abstract GameStates getRoundState();
	
	public void forcedBetBeforeStarting(){
		isSmallBlindBetDone=true;
		isBigBlindBetDone=true;
	}

}
