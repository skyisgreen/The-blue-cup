import java.util.*;
public class Cou {
//暴力解法
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a,b,c,d,e,f,g,h,i;
		int s=0;//次数
		for(a=1;a<10;a++)
		{
			for(b=1;b<10;b++)
			{
				if(b==a)
					continue;
				for(c=1;c<10;c++)
				{
					if(c==a||c==b)
						continue;
					for(d=1;d<10;d++)
					{
						if(d==a||d==b||d==c)
							continue;
						for(e=1;e<10;e++)
						{
							if(e==a||e==b||e==c||e==d)
								continue;
							for(f=1;f<10;f++) 
							{
								if(f==a||f==b||f==c||f==d||f==e)
									continue;
								for(g=1;g<10;g++)
								{
									if(g==a||g==b||g==c||g==d||g==e||g==f)
										continue;
									for(h=1;h<10;h++)
									{
										if(h==a||h==b||h==c||h==d||h==e||h==f)
											continue;
										for(i=1;i<10;i++)
										{
											if(i==a||i==b||i==c||i==d||i==e||i==f||i==g||i==h)
												continue;
	//两个整数相除得到的还是整数，所以要把其中一个整数先乘以1.0转化成浮点数（分子上不易出错）
					double num =a+b*1.0/c+(100*d+10*e+f)*1.0/(100*g+10*h+i)-10;
					if(num==0)
					{
						s++;
					}
										}
									}
									
								}
							}
						}
					}
				}
			}
		} 
		System.out.println(s);

	}

}
