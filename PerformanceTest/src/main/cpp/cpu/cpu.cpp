// Cpu.cpp : Defines the entry point for the console application.
//

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <string.h>
#include <math.h>
#include <iostream>

int val;
#define MAX_VAL 10000
int main(int argc, char* argv[])
{

	srand((unsigned)time(NULL));

        int i = 0;
        while(1)
	{
               i++;
               val = rand();
	       val = (int)((float)val/(float)RAND_MAX*(float)MAX_VAL);		

               sin(18358);
               sinf(0.9827);
               sqrt(5984752);
               sqrt(val*7);
               sqrt(val*5);
               sqrt(val);
               tan(val);
               pow(val,val);
               pow(val,6000);
               cos(val);
               //std::cout<<"@@@@@@@@@: "<< i <<std::endl;
               if(i > 200000)
		{ 
               i = 0;
               //usleep(10);
		}
	}


	return 0;
}
