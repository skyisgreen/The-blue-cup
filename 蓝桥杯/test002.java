
public class Test002 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//从0岁开始 不断的测试 若达到236则输出 否则跳转重新循环，最终要的是开始的年龄 而不是目前几岁
		for(int i=1;i<=100;i++)
		{
			int sum =0;//蜡烛数量
			int age =i;
			while(true)
			{
				sum+=age;
				age++;
				if(sum==236)
				{
					System.out.println(i);
					break;
				}
				if(sum>236)
				break;
			}

		}

	}

}
