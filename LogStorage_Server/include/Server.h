#ifndef _SERVER_H
#define	_SERVER_H

#include <string.h>  
#include <stdio.h>  
#include <stdlib.h>  
#include <unistd.h>  
#include <sys/select.h>  
#include <sys/time.h>  
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <netinet/in.h>  
#include <arpa/inet.h>  
#include <sys/epoll.h>  
#include <errno.h>  

#define OPEN_MAX 100  

int sockfd;
int fd[OPEN_MAX]; 
int fd_info;
int fd_log[4];
static char command[8196] = {0};
struct epoll_event event;   // 告诉内核要监听什么事件    
struct epoll_event wait_event; //内核监听完的结果  
static int index_txt = 0;
int epfd;
//创建socket对象
int SocketInit();
//初始化服务器配置
int ServerInit();
//文件IO操作
void OpenFile();
//对已连接的客户端的数据处理
void communication();


#endif
