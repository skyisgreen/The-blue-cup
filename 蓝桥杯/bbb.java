
public class bbb{
    static int m = 0;

    static boolean judge(int x, int y)
    {
        if (x - y == -1 || y - x == -1) return true;
        return false;//判断并返回 
    }

    static void find(int num[], int n)
    {
        int i, j, flag;
        if (n >= 10)
        {
            m++;
        }

        for (i = 0; i <= 9; i++)//对每个数字进行判断 
        {
            //先排除相等的情况 
            flag = 0;
            for (j = 0; j < n; j++)
            {
                if (num[j] == i)
                {
                    flag = 1;
                    break;
                }
            }
            if (1 == flag) continue;
            //再排除相邻的情况
            switch (n)
            {
                case 0:
                    break;
                case 1:
                    if (judge(num[0],i)) flag = 1;
                    break;
                case 2:             
                    if (judge(num[1],i)) flag = 1;
                    break;
                case 3:
                    if (judge(num[0],i)) flag = 1;
                    break;
                case 4:
                    if (judge(num[0], i) || judge(num[1],i)|| judge(num[3],i)) flag = 1;
                    break;
                case 5:
                    if (judge(num[0], i) || judge(num[1],i) || judge(num[2],i)|| judge(num[4],i)) flag = 1;
                    break;
                case 6:
                    if (judge(num[1], i) || judge(num[2], i)|| judge(num[5], i)) flag = 1;
                    break;
                case 7:
                    if (judge(num[3], i) || judge(num[4], i)) flag = 1;
                    break;          
                case 8:
                    if (judge(num[3], i) || judge(num[4], i) || judge(num[5], i)|| judge(num[7], i)) flag = 1;
                    break;
                case 9:
                    if (judge(num[4], i) || judge(num[5], i) || judge(num[6], i)|| judge(num[8], i)) flag = 1;
                    break;
            }
            if (flag == 1) continue; 
            num[n] = i;
            find(num, n + 1);
        }
    }

    public static void main(String[] args) {
        int num[] = new int[10];
        find(num, 0);   
        System.out.println(m);

    }

}
