package com.temportalist.compression.common.lib;

import com.temportalist.compression.common.config.Config;

/**
 * Created by TheTemportalist on 4/14/2016.
 *
 * @author TheTemportalist
 */
public enum EnumTier {

    // Process time determined by desmos.com
    /* Near exponential
    00, 0.5, (<-)*20,
    01, 0.75, (<-)*20,
    02, 1, (<-)*20,
    03, 5, (<-)*20,
    04, 30, (<-)*20,
    05, 70, (<-)*20,
    06, 100, (<-)*20,
    07, 125, (<-)*20,
    08, 150, (<-)*20,
    09, 200, (<-)*20,
    10, 250, (<-)*20,
    11, 300, (<-)*20,
    12, 400, (<-)*20,
    13, 500, (<-)*20,
    14, 600, (<-)*20,
    15, 800, (<-)*20,
    16, 1000, (<-)*20,
    17, 1200, (<-)*20,
    */
	SINGLE(				"Single",			9L, 10),
	DOUBLE(				"Double",			81L, 15),
	TRIPLE(				"Triple",			729L, 20),
	QUADRUPLE(			"Quadruple",		6561, 100),
	QUINTUPLE(			"Quintuple",		59049L, 600),
	HEXTUPLE(			"Hextuple",			531441L, 1400),
	SEPTUPLE(			"Septuple",			4782969L, 2000),
	OCTUPLE(			"Octuple",			43046721L, 2500),
	NONUPLE(			"Nonuple",			387420489L, 3000),
	DECUPLE(			"Decuple",			3486784401L, 4000),
	UNDECUPLE(			"Undecuple",		31381059609L, 5000),
	DUODECUPLE(			"Duodecuple",		282429536481L, 6000),
	TREDECUPLE(			"Tredecuple",		2541865828329L, 8000),
	QUATTUORDECUPLE(	"Quattuordecuple",	22876792454961L, 10000),
	QUIDECOUPLE(		"Quindecouple",		205891132094649L, 12000),
	SEDECOUPLE(			"Sedecouple",		1853020188851841L, 16000),
	SEPTENDECOUPLE(		"Septendecouple",	16677181699666570L, 20000),
	DUODEVDECUPLE(		"Duodevdecouple",	150094635296999136L, 24000);

	private final String name;
	private final Long sizeMax;
	private int timeInTicksToCompressTo;
	private int timeInTicksToDecompressFrom;

	EnumTier(String name, long size, int processingTicks) {
		this.name = name;
		this.sizeMax = size;
		this.timeInTicksToCompressTo = processingTicks;
		this.timeInTicksToDecompressFrom = processingTicks;
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

	public EnumTier getPrev() {
		return this != EnumTier.getHead() ? EnumTier.getTier(this.ordinal() - 1) : null;
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
		else return a.compareTo(b); // if a > b: +x else a < b: -x
	}

	public boolean lte(EnumTier b) {
		return EnumTier.compare(this, b) <= 0;
	}

	public boolean lt(EnumTier b) {
		return EnumTier.compare(this, b) < 0;
	}

	public void getConfig(Config config, String category) {
        this.timeInTicksToCompressTo = config.get(category, String.format ("%02d", this.ordinal()) + ".timeToCompress",
                this.getName() + ": The time it takes (in ticks, 20 = 1 second) to compress the previous tier into this tier.",
                this.timeInTicksToCompressTo
        ).getInt();
        this.timeInTicksToDecompressFrom = config.get(category, String.format ("%02d", this.ordinal()) + ".timeToDecompress",
				this.getName() + ": The time it takes (in ticks, 20 = 1 second) to decompress from this tier into the previous tier.",
                this.timeInTicksToDecompressFrom
        ).getInt();
	}

	public int getTicksToCompressTo() {
		return this.timeInTicksToCompressTo;
	}

	public int getTicksToDecompressFrom() {
		return this.timeInTicksToDecompressFrom;
	}

}
