package com.temportalist.compression.common.lib;

/**
 * Created by TheTemportalist on 4/14/2016.
 *
 * @author TheTemportalist
 */
public enum EnumTier {

	SINGLE(				"Single",			9L),
	DOUBLE(				"Double",			81L),
	TRIPLE(				"Triple",			729L),
	QUADRUPLE(			"Quadruple",		6561),
	QUINTUPLE(			"Quintuple",		59049L),
	HEXTUPLE(			"Hextuple",			531441L),
	SEPTUPLE(			"Septuple",			4782969L),
	OCTUPLE(			"Octuple",			43046721L),
	NONUPLE(			"Nonuple",			387420489L),
	DECUPLE(			"Decuple",			3486784401L),
	UNDECUPLE(			"Undecuple",		31381059609L),
	DUODECUPLE(			"Duodecuple",		282429536481L),
	TREDECUPLE(			"Tredecuple",		2541865828329L),
	QUATTUORDECUPLE(	"Quattuordecuple",	22876792454961L),
	QUIDECOUPLE(		"Quindecouple",		205891132094649L),
	SEDECOUPLE(			"Sedecouple",		1853020188851841L),
	SEPTENDECOUPLE(		"Septendecouple",	16677181699666570L),
	DUODEVDECUPLE(		"Duodevdecouple",	150094635296999136L);

	private final String name;
	private final Long sizeMax;

	EnumTier(String name, long size) {
		this.name = name;
		this.sizeMax = size;
	}

	public String getName() {
		return this.name;
	}

	public long getSizeMax() {
		return this.sizeMax;
	}

	public EnumTier getNext() {
		return this != EnumTier.getTail() ? EnumTier.getTier(this.ordinal() + 1) : null;
	}

	public static int getQuantity() {
		return EnumTier.values().length;
	}

	public static EnumTier getTier(int ordinal) {
		if (ordinal < 0) return null;
		return EnumTier.values()[ordinal % EnumTier.getQuantity()];
	}

	public static String getName(int ordinal) {
		return EnumTier.getTier(ordinal).getName();
	}

	public static long getSizeCap() {
		return EnumTier.getTail().getSizeMax();
	}

	public static EnumTier getHead() {
		return EnumTier.SINGLE;
	}

	public static EnumTier getTail() {
		return EnumTier.DUODEVDECUPLE;
	}

	public static int compare(EnumTier a, EnumTier b) {
		if (a == null && b==null) return 0;
		else if (a != null && b == null) return 1;
		else if (a == null) return -1;
		else return a.compareTo(b);
	}

	public boolean lte(EnumTier b) {
		return EnumTier.compare(this, b) >= 0;
	}

}
