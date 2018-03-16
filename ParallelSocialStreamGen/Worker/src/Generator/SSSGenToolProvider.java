/**   
* @Title: SSSGenToolProvider.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年5月26日 
* @version V1.0   
*/
package Generator;


/** 
* @author Chengcheng Yu
* @version 2016年5月26日 下午1:33:00
*/
/** 
* @ClassName: SSSGenToolProvider 
* @Description: TODO
* @author Chengcheng Yu
*  
*/
public class SSSGenToolProvider {
	public static AbstractSSSGenTool createSSSGenTool(){

		System.out.println("Worker createSSSGenTool start.");
		AbstractSSSGenTool result =null;
		try {
//			Parameter para = new Parameter(ip);
			if (Parameter.workerInfo.getDataType().equals("weibo")) {
				System.out.println("Worker createSSSGenTool end.");
				result= new TweetGenToolFactory();
			} else if (Parameter.workerInfo.getDataType().equals("patent")) {
				System.out.println("Worker createSSSGenTool end.");
				result= new PatentGenToolFactory();
			} else {
				System.err.println("There is no such data type of " + Parameter.workerInfo.getDataType()
						+ ". Please input correct date type.");
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;

	}
}
