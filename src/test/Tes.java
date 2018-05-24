package test;

public class Tes {
	 public static void Sort_Forall_IDDWhile(int[] a)
     {
	int n = a.length;
	while (0 <= --n)
         {
		int j = 0;
		while(j < n)
   		  {
                 if (a[j + 1] < a[j])
                 {
                     int tmp = a[j]; 
						a[j] = a[j + 1];
					    a[j + 1] = tmp;
				tmp++;
                 }
				j++;        
			  }
         
	}
	n = 12345;
	n = n*2;
 }

}

