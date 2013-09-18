package util;

import java.util.HashMap;
import java.util.Map;

public class Configuration {


public static int MONEY_1=500;
public static int MONEY_2=650;

public static int SMALL_BLIND=10;
public static int BIG_BLIND=20;

public static int TOTAL_PLAYER=6;

public static Map<Integer, Integer> playerVSMoney=new HashMap<Integer, Integer>();

static{
	
	playerVSMoney.put(1, MONEY_1);
	playerVSMoney.put(2, MONEY_2);
	playerVSMoney.put(3, MONEY_1);
	playerVSMoney.put(4, MONEY_1);
	playerVSMoney.put(5, MONEY_2);
	playerVSMoney.put(6, MONEY_2);
	
}



}
