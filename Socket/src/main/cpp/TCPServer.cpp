/* 
 * File:   main.cpp
 * Author: Administrator
 *
 * Created on 2015年11月1日, 下午2:43
 */
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <errno.h>
#include <dirent.h>
#include <string>
#include <string.h>
#include <vector>
#include <fstream>

#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <arpa/inet.h>
#include <netdb.h>

using namespace std;

#define PORT 12345
#define BACKLOG 10
#define BUFFER_SIZE 1024

vector<string> enum_files(string d_path) {
    DIR *dir;
    dirent *dirent;
    if ((dir = opendir(d_path.c_str())) == NULL) {
        perror("open directory failed");
        exit(1);
    }

    vector<string> f_paths;
    while ((dirent = readdir(dir)) != NULL) {
        if ((strcmp(dirent->d_name, ".")) == 0 || strcmp(dirent->d_name, "..") == 0 || dirent->d_type == DT_DIR) {
            continue;
        }
        f_paths.push_back(d_path + string(dirent->d_name));
        //        printf("%6d:%-19s %5s\n",dirent->d_ino,dirent->d_name,(dirent->d_type==DT_DIR)?("(DIR)"):(""));
    }

    //        for (vector<string>::iterator it = f_paths.begin(); it != f_paths.end(); it++) {
    //            cout << *it << endl;
    //        }
    return f_paths;
}

char* read_file(string f_path) {
    filebuf *fbuf;
    ifstream is;
    long size;
    char *buffer;
    is.open(f_path.c_str(), ios::binary);
    fbuf = is.rdbuf();
    size = fbuf->pubseekoff(0, ios::end, ios::in);
    fbuf->pubseekpos(0, ios::in);
    buffer = new char[size];
    fbuf->sgetn(buffer, size);
    is.close();
    //delete[] buffer;
    //cout << "read " << f_path << " completed" << endl;
    return buffer;
}

void start_server() {
    int sockfd, client_fd;
    struct sockaddr_in local_address, server_address;

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("socket create failed");
        exit(1);
    }

    local_address.sin_port = htons(PORT);
    local_address.sin_family = AF_INET;
    local_address.sin_addr.s_addr = INADDR_ANY;

    bzero(&(local_address.sin_zero), 8);
    if (bind(sockfd, (struct sockaddr*) &local_address, sizeof (struct sockaddr)) == -1) {
        perror("bind error");
        exit(1);
    }

    if (listen(sockfd, BACKLOG) == -1) {
        perror("listen error");
        exit(1);
    }

    while (1) {
        int sin_size = sizeof (struct sockaddr_in);
        if ((client_fd = accept(sockfd, (struct sockaddr*) &server_address, (socklen_t*) & sin_size)) == -1) {
            perror("accept error!");
            continue;
        }

        printf("received a connection from %s\n", (char*) inet_ntoa(server_address.sin_addr));

        if (!fork()) {
            int read_length;
            char buffer[BUFFER_SIZE];
            if ((read_length = read(client_fd, buffer, BUFFER_SIZE)) < 0) {
                perror("reading stream error");
                continue;
            }

            printf("%s\n", buffer);

            string msg = "hello, you are connected";
            if (send(client_fd, msg.c_str(), msg.length(), 0) == -1) {
                perror("send error");
            }
            close(client_fd);
            exit(0);
        }
        close(client_fd);
    }
}



int main(void) {
    vector<string> f_paths = enum_files("/home/sg/lf489159/Pictures/");
    char* fs_buffer[f_paths.size()];
    int index = 0;
    for (vector<string>::iterator it = f_paths.begin(); it != f_paths.end(); it++) {
        fs_buffer[index] = read_file(*it);
        index++;
    }
    return 0;
}



