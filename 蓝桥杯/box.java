public class box{  
        static int count = 0;  

        static void swap(char array[], int a, int b) {    
            char temp = array[a];    
            array[a] = array[b];    
            array[b] = temp;    
        }    

        static boolean check(char [] str) {    
            if(check2(str[0], str[1]) && check2(str[0], str[3]) && check2(str[0], str[4]) && check2(str[0], str[5])     
                     && check2(str[1], str[2]) && check2(str[1], str[4]) && check2(str[1], str[5]) && check2(str[1], str[6])    
                     && check2(str[2], str[5]) && check2(str[2], str[6])    
                     && check2(str[3], str[4]) && check2(str[3], str[7]) && check2(str[3], str[8])    
                     && check2(str[4], str[5]) && check2(str[4], str[7]) && check2(str[4], str[8]) && check2(str[4], str[9])    
                     && check2(str[5], str[6]) && check2(str[5], str[8]) && check2(str[5], str[9])    
                     && check2(str[6], str[9]) && check2(str[7], str[8]) && check2(str[8], str[9]))    
                return true;    
            return false;    
        }  

        static boolean check2(char c, char d) {    
            // TODO Auto-generated method stub    
            if(c == (d+1) || c == (d-1))    
                return false;    
            return true;    
        }  

        static void permutation(char[] str, int a, int length){    
            if(a == length){    
                if(check(str)){    
                    count++;    
                    System.out.println(String.valueOf(str));    
                }    
            }else{    
                for(int i = a; i <= length; ++i){    
                    swap(str, i, a);    
                    permutation(str, a + 1, length);  
                    swap(str, i, a);    
                }    
            }    
        }  

        public static void main(String[] args) {    
            char[] str = "0123456789".toCharArray();    
            permutation(str, 0, 9);    
            System.out.println(count);    
        }    
    }  
