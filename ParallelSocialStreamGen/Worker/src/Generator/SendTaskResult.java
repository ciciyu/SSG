/**   
* @Title: SendTaskResult.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年6月16日 
* @version V1.0   
*/
package Generator;

import java.util.concurrent.CountDownLatch;

import Object.TaskResult;
import Object.WorkerTask;

/** 
* @author Chengcheng Yu
* @version 2016年6月16日 下午7:14:46
*/
/** 
* @ClassName: SendTaskResult 
* @Description: TODO
* @author Chengcheng Yu
*  
*/
public class SendTaskResult implements Runnable {
	CountDownLatch threadSignal;
	public SendTaskResult(CountDownLatch threadSignal){
		this.threadSignal =threadSignal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				WorkerTask task = Parameter.tasks.getFirstTask();
				if(task!=null){
					
						TaskResult result = Parameter.genTool.getTaskLink(task);
						Parameter.io.writeTaskResult(result);
				}else{
					Parameter.io.writeSendTaskResultEnd();
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threadSignal.countDown();
	}

}
