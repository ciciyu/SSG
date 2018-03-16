package Generator;

/** 
 * The SSGenUtilFactory is used to create various social streams' generator tool.
 * @author Chengcheng Yu
 */
public class SSSGenToolProvider {

	public static AbstractSSSGenTool createSSSGenTool(String path) throws Exception {
		new Parameter(path);
		if (Parameter.dataType.equals("weibo")) {
			return new TweetGenToolFactory();
		} else if (Parameter.dataType.equals("patent")) {
			return new PatentGenToolFactory();
		} else if (Parameter.dataType.equals("email")) {
			return new EmailGenToolFactory();
		}else{
			System.err.println("There is no such data type of " + Parameter.dataType
					+ ". Please input correct date type.");
			return null;
		}

	}

}
