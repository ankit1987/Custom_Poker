package util;

import java.util.ArrayList;
import java.util.List;

/**
 * this describes available bet option
 * @author ivy4127
 *
 */
public enum BetOption {

	CHECK, BET,FOLD,CALL,RAISE,ALL_IN;

 public static List<BetOption> allBetOptions=new ArrayList<BetOption>();

 static{
		allBetOptions.add(CHECK);
		allBetOptions.add(BET);
		allBetOptions.add(FOLD);
		allBetOptions.add(CALL);
		allBetOptions.add(RAISE);
		allBetOptions.add(ALL_IN);
 }


}
