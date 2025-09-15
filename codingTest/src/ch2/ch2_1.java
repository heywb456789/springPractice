package ch2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ch2_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] scs = sc.nextLine().split(",");
        int[] arr = new int[scs.length];

        for(int i=0;i<scs.length;i++){
            arr[i] = Integer.parseInt(scs[i]);
        }

        int flag = sc.nextInt();

        int lt = 0;
        int rt = 0;
        int sum = 0;
        int count =0;

        while(rt < arr.length){
            sum += arr[rt++];
            while(sum > flag){
                sum -= arr[lt++];
            }

            if(sum == flag){
                count++;
            }
        }
        System.out.println(count);


    }
}
