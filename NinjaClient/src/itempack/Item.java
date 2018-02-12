package itempack;

public interface Item {
	final static int POWERUP=0;
	final static int HOLD=1;
	final static int DOUBLEATTACK=2;
	final static int MULTIATTACK=3;
	final static int SEETHROUGHALL=4;
	final static int SHIELD=5;
	final static int MOVEUP=6;
	final static int TELEPORT=7;
	final static int ENERGY=8;
	final static int SEETHROUGH=9;

	public void activate();
}
