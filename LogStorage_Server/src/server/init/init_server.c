#include "Server.h" 

int ServerInit()
{ 
    memset(fd,-1, sizeof(fd));  
    fd[0] = sockfd;  
    fd[1] = 0;  
    epfd = epoll_create(10); // 创建一个 epoll 的句柄，参数要大于 0， 没有太大意义    
    if( -1 == epfd ){    
        perror ("epoll_create");    
        return -1;    
    }    

    event.data.fd = 0;     //监听标准输入    
    event.events = EPOLLIN; // 表示对应的文件描述符可以读  
      
    //5.事件注册函数，将标准输入描述符 0 加入监听事件    
    int ret = epoll_ctl(epfd, EPOLL_CTL_ADD, 0, &event);    
    if(-1 == ret){    
        perror("epoll_ctl");    
        return -1;    
    } 	
	  
    event.data.fd = sockfd;     //监听套接字    
    event.events = EPOLLIN; // 表示对应的文件描述符可以读  
      
    //5.事件注册函数，将监听套接字描述符 sockfd 加入监听事件    
    ret = epoll_ctl(epfd, EPOLL_CTL_ADD, sockfd, &event);    
    if(-1 == ret){    
        perror("epoll_ctl");    
        return -1;    
    }
	
	return 0;
}
