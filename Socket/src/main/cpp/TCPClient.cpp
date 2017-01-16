/* 
 * File:   main.cpp
 * Author: lf489159
 *
 * Created on November 5, 2015, 10:49 AM
 */

#include <cstdlib>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string>
#include <string.h>

#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <arpa/inet.h>
#include <netdb.h>

using namespace std;

#define PORT 12345
#define IP "127.0.0.1"
#define BUFFER_SIZE 100

/*
 * 
 */
int main(int argc, char** argv) {

    int sockfd, receive_length;
    char buffer[BUFFER_SIZE];
    struct sockaddr_in server_address;

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("socket error");
        exit(1);
    }
    bzero(&server_address, sizeof (server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(PORT);
    server_address.sin_addr.s_addr = inet_addr(IP);

    if (connect(sockfd, (struct sockaddr*) &server_address, sizeof (struct sockaddr)) == -1) {
        perror("connect error");
        exit(1);
    }
    
    string msg = "this is a client message";
    write(sockfd, msg.c_str(), msg.length());
    if ((receive_length = recv(sockfd, buffer, BUFFER_SIZE, 0)) == -1) {
        perror("receive error");
        exit(1);
    }

    buffer[receive_length] = '\0';
    printf("received: %s", buffer);
    close(sockfd);

    return 0;
}

