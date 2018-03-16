package Generator;

/**
 * @author Chengcheng Yu
 */
public class Statistic {
//	public static Integer itemNum = 0;
	public static Integer linkNum = 0;

	public static Integer IO_Write = 0;
	public static Integer IO_read = 0;
	public static Integer findNum = 0;
	public static long UsedMemory = 0;
	public static long baseMemory = 0;

	public static void setUsedMemory(long memory) {
		if (memory > UsedMemory) {
			UsedMemory = memory;
		}
	}

	public static void outPutStatistic() {
		double runningTime = (System.currentTimeMillis() - Parameter.runningStart) / 1000f;
		System.out.println("itemNum: " + Parameter.IDGen.lastValue());
		System.out.println("linkNum: " + Statistic.linkNum);
		System.out.println("running time: " + runningTime);
		System.out.println("Throught(item) : " + (double) Parameter.IDGen.lastValue() / runningTime
				+ " items/seconds ");
		
		System.out.println("UsedMemory : " + Statistic.UsedMemory* 9.5367e-7 + "Mb");
	}

}
