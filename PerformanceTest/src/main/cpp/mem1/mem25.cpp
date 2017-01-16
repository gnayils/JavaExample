// Cpu.cpp : Defines the entry point for the console application.
//

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <string.h>
#include <math.h>

#define MAX_VAL  250
#define _GG_ (1024*1024*500)
#define _MEMSET_SIZE_ (1024*1024*30)

int val = 0;
char *p = NULL;
char *pg0 = NULL;

int main(int argc, char* argv[])
{
	
	srand((unsigned)time(NULL));

	pg0 = (char*)malloc(_GG_);
	if(!pg0)
	{
		printf("error pg0\n");
		exit(0);
	}

	while(1)
	{
		val = rand();
		val = (int)((float)val/(float)RAND_MAX*(float)MAX_VAL);		
/*
                memset(pg0+(int)(float)val/(float)val/MAX_VAL*(_GG_-_MEMSET_SIZE_)),0,_MEMSET_SIZE_);
*/
                meset(pg0,0,_MEMSET_SIZE_);
                if(!val)
                	continue;
                p = (char*)malloc(val*1024*1024);
                if(!p)
                {
                  continue;	
                }
		
		for(int z=0;z<1000;z++)
			memset(p,0,val);
		free(p);
	}
	
	return 0;
}
