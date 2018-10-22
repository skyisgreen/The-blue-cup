package skyisgreen;

public class Cou2 {
//解法二：全排列递归
	 static  int sum=0;
	//例如：abc
	//首先锁定第一位 对bc进行全排列 得 a bc ； a cb
	//将a和b交换 锁定第一位b 得 b ac ； b ca；
	//不断的进行递归排序得到最终的全排列
	static void f(int[]aa,int k)
	{
		if(k==aa.length-1)
		{
			if(aa[0]+aa[1]*1.0/aa[2]+(aa[3]*100+aa[4]*10+aa[5])*1.0/(aa[6]*100+aa[7]*10+aa[8])-10 ==0)
			{
				sum +=1;
			}	
		}
		for(int i=k;i<aa.length;i++)
		{
			{int t=aa[k]; aa[k]=aa[i];aa[i]=t;}//试探
			f(aa,k+1);
			{int t=aa[k]; aa[k]=aa[i];aa[i]=t;}//回溯
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int aa [] = {1,2,3,4,5,6,7,8,9};
		
		f(aa,0);
		System.out.println(sum);

   }
}
