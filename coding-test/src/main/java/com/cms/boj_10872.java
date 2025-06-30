package com.cms;

import java.util.Scanner;

/**
 * 0보다 크거나 같은 정수 N이 주어진다. 이때, N!을 출력하는 프로그램을 작성하시오.
 *
 * 입력
 * 첫째 줄에 정수 N(0 ≤ N ≤ 12)이 주어진다.
 *
 * 출력
 * 첫째 줄에 N!을 출력한다.
 *
 * 예제 입력 1      예제 출력 1
 * 10              3628800
 * 예제 입력 2      예제 출력 2
 * 0               1
 */
public class boj_10872 {

    public static int factorial(int n){
        if(n==0){return 1;}
        return n*factorial(n-1);
}


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        System.out.println(factorial(n));
    }

}
