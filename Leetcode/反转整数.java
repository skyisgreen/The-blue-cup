import java.util.Scanner;

class 反转整数 {
   public static void main (String[] args)
   {
       Scanner sc = new Scanner(System.in);
       System.out.println("请输入的一个整数");
       int num=sc.nextInt();
       int result=0;//存反转的数字

       while(true)
       {
           int n=num%10;//取出最低位上的数字
           result=result*10+n;//依次的反转存储得到反转的数字
           num=num/10;//降位
           if(num==0)
           {
               break;
           }
       }
       System.out.println(result);

   }


}
