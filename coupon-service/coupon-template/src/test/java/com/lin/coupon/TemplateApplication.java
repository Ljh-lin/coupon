package com.lin.coupon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateApplication {



    @Test
    public void contextLoad(){
        int i=0;
        i=i++ +i;
        System.out.println("I="+i);
    }

    @Test
    public void test(){
        String str="";
        System.out.println(str.split(",").length);
    }

    @Test
    public void test1(){
        int b[]={9,1,4,2,3,6,5,8,7};
        for (int i=0;i<b.length;i++){
            System.out.printf("%4d",b[i]);
        }
        System.out.println();
        fun(b,b.length);
        for (int i=0;i<b.length;i++){
            System.out.printf("%4d",b[i]);
        }
    }

    private void fun(int a[],int n){
        int i,j,max,min,px,pn,t;
        for (i = 0; i <n-1 ; i++) {
            max=min=0;
            px=pn=i;
            for (j=i+1;j<n;j++){
                if (max<a[j]){
                    max=a[j];
                    px=j;
                }
                if (min>a[j]){
                    min=a[j];
                    pn=j;
                }
            }
            if (pn!=j){
                t=a[i];
                a[i]=min;
                a[pn]=t;
                if (px==i){
                    px=pn;
                }
            }
            if (px!=i+1){
                t=a[i+1];
                a[i+1]=max;
                a[px]=t;
            }

        }
    }

    @Test
    public void test03(){
        Scanner scanner=new Scanner(System.in);
        int N=scanner.nextInt();
        int[] nums=new int[N];
        for (int i=0;i<N;i++){
            nums[i]=scanner.nextInt();
        }

        int max=nums[0],sum=0;
        for (int i = 0; i <nums.length ; i++) {
            sum+=nums[i];
            //更新
            if (sum>max)
                max=sum;
            if (sum<0)
                sum=0;
        }
        System.out.println(max);
    }
}



