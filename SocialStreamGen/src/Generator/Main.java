package Generator;

public class Main {
	public static void main(String[] args) throws Exception {
		String paramaterPath = "/Users/ycc/kuaipan/workspace/SocialStreamGen/";
		SSSGenerator sssg = new SSSGenerator(paramaterPath);
		Parameter.runningStart = System.currentTimeMillis();
		sssg.generateSocialStream();
		
	}
}