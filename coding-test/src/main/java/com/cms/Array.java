package com.cms;

import java.util.Arrays;

/**
 * @author : MinjaeKim
 * @packageName : com.cms
 * @fileName : Array
 * @date : 2025-06-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class Array {

    public static int[] reverseArray(int[] arr){
        int i= 0 ;
        int j = arr.length-1;
        while (i<j){
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++; j--;
        }
        return arr;
    }


    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        reverseArray(arr);
        System.out.println(Arrays.toString(arr));

        int[][] arr2 = {{1,2},{2,3},{3,4},{5,6},{7,8}};
        int total = sumArray(arr2);
        System.out.println(total);
    }

    private static int sumArray(int[][] arr2) {
        int total = 0;
        for (int[] arr : arr2) {
            for(int a : arr){
                total += a;
            }
        }
        return total;
    }

}
