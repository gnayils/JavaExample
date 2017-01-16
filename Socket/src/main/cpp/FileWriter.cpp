/* 
 * File:   main.cpp
 * Author: lf489159
 *
 * Created on November 5, 2015, 2:49 PM
 */

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sys/time.h>
#include <netdb.h>
#include <bits/basic_string.h>

using namespace std;

int main(int argc, char** argv) {
    uint8_t* processedImage = new uint8_t[5];
    processedImage[0] = 90;
    processedImage[1] = 91;
    processedImage[2] = 92;
    processedImage[3] = 93;
    processedImage[4] = 94;
    for (int i = 0; i < 50; i++) {
        usleep(30000);
        if (rand() % 25 < 2) {
            struct timeval tv;
            gettimeofday(&tv, NULL);
            long time = tv.tv_sec * 1000 + tv.tv_usec / 1000;

            string path;
            path.append("/home/sg/lf489159/Documents/image.");
            char timechar[1000];
            sprintf(timechar, "%ld", time);
            path.append(timechar);

            FILE *fp = NULL;
            fp = fopen(path.c_str(), "wb");
            if (fp != NULL) {
                fwrite(&processedImage[0], 5 * sizeof (uint8_t), 1, fp);
                fclose(fp);
            }
        }
    }

    return 0;
}

