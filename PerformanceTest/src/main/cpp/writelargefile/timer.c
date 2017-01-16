#include "header.h"

static struct sigaction old_alarm_handler;

static struct itimerval old_timer;

static unsigned long thickcount;

static void sig_handler (int v)
{
	thickcount++;
}

int init()
{
    struct itimerval timerv;
    struct sigaction siga;

    siga.sa_handler= sig_handler;
    siga.sa_flags  = 0;
    
    memset (&siga.sa_mask, 0, sizeof (sigset_t));
	
    sigaction (SIGALRM, &siga, &old_alarm_handler);
	
    timerv.it_interval.tv_sec = 0;
    timerv.it_interval.tv_usec= 10000*1; //10 ms
    timerv.it_value = timerv.it_interval;
	
    if(setitimer (ITIMER_REAL, &timerv, &old_timer))
	{
        fprintf(stderr, "TIMER: setitimer call failed!\n");
		return 0;
    }
	
    return 1;
}

void TerminateTimer ()
{
    int i;

    if (setitimer (ITIMER_REAL, &old_timer, 0) == -1) 
	{
        fprintf( stderr, "TIMER: setitimer call failed!\n");
        return;
    }
	
    if (sigaction (SIGALRM, &old_alarm_handler, NULL) == -1)
	{
        fprintf( stderr, "TIMER: sigaction call failed!\n");
		return;
    }
}

/////////////////////////////////////////////////////////////////

unsigned long GetCurTime()
{
	int minute;
	int second;
    time_t timep;
	struct tm *ptime;
	struct timeb timeb;
	
	time(&timep);
	ptime = localtime(&timep);
	
	minute= ptime->tm_min;
	second= ptime->tm_sec;
	
	return minute*60+second;
}
