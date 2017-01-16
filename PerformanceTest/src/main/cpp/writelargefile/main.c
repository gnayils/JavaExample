/********************************************
  date :
     2008.09.30

  comment:

     Test each writ engine speed.
  
   type:
     write O_DIRECT
 
   way:
     continue write 600M data,each time 2M. 
*********************************************/

#define _GNU_SOURCE
#define _LARGEFILE_SOURCE
#define _LARGEFILE64_SOURCE
#define _FILE_OFFSET_BITS 64
#include "header.h"
 
int main() 
{
    int fd;
    int ret;
    char *buffer;
    int page_size;

    page_size = getpagesize();
 
    ret = posix_memalign((void**)&buffer,512,512*page_size);
    if(ret)
    {
      printf("posix_memalign error:%s\n",strerror(ret));
      exit(-5);
    }

while(1)
{

    system("rm -rf /cine_file/testfile.dat");
    fd = open("/cine_file/testfile.dat", O_WRONLY/*O_RDWR*/|O_CREAT|O_DIRECT|O_LARGEFILE,S_IRWXU);
    if(-1 == fd)
    {
        printf("write file error\rn");
        exit(-1);
    }

    memset(buffer,0x30,512*page_size);
    buffer[512*page_size-1] = 0x31;

    int i;
    for(i=0;i<2048;i++)
    {
       ret = write(fd,buffer,512*page_size);
       if(-1 == ret)
       {
         printf("write error\n");
         exit(-2);
       }
    }
    close(fd);

    fd = open("/cine_file/testfile.dat", O_RDONLY|O_CREAT|O_DIRECT|O_LARGEFILE,S_IRWXU);
    if(-1 == fd)
    {
        printf("open file error\rn");
        exit(-1);
    }
    for(i=0;i<2048;i++)
    {
       ret = read(fd, buffer,512*page_size);
       if(-1 == ret)
       {
          printf("error read data\n");
          exit(-2);
       }
    }
    close(fd);
}

    return 0;
}
