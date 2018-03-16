/**   
* @Title: test.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年12月6日 
* @version V1.0   
*/
package Generator;
/** 
* @author Chengcheng Yu
* @version 2016年12月6日 下午12:15:42
*/
/** 
* @ClassName: test 
* @Description: TODO
* @author Chengcheng Yu
*  
*/
public class test {

	/** 
	* @Title: main 
	* @Description: TODO
	* @param @param args    
	* @return void    
	* @throws 
	*/
	public static void main(String[] args) {
		int[] a = {52,39,67,95,70,8,25};  
		int i = 0, j = a.length - 1;//步骤2:初始化i,j为序列的起始和终止下标
	    new test().quickSort(a,i,j); 
	}
	
	int partition(int a[], int i, int j) {
		int privotKey = a[i]; // 基准元素
		while (i < j) { // 从表的两端交替地向中间扫描
			/* 步骤3:从j的位置开始由后向前依次搜索， 
			 * 当找到比基准元素小的元素时，将该元素移动到i的位置，然后i=i+1; */
			while (i < j && a[j] >= privotKey) j--;
			if (i < j) {
				a[i] = a[j];
				i++;
			}
			/* 步骤4:从i的位置开始由后向前依次搜索， 
			 * 当找到比基准元素大的元素时，将该元素移动到j的位置，然后j=j-1;*/
			while (i < j && a[i] <= privotKey) i++;
			if (i < j) {
				a[j] = a[i];
				j--;
			}
		}
		a[i] = privotKey;
		return i;
	}
	
	public void quickSort(int a[], int i, int j) {
		if(i<j){
			int loc = partition(a, i, j); // 一趟快速排序
			quickSort(a, i, loc - 1); // 对小于基准元素的子区间递归排序
			quickSort(a, loc + 1, j); // 对大于基准元素的子区间递归排序
		}
	}
	

}
