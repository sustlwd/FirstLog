#include "Server.h" 

int SocketInit()
{
	//1.创建tcp监听套接字  
    sockfd = socket(AF_INET, SOCK_STREAM, 0);  
      
    //2.绑定sockfd  
    struct sockaddr_in my_addr;  
    bzero(&my_addr, sizeof(my_addr)); 
    my_addr.sin_family = AF_INET;  
    my_addr.sin_port = htons(8668);  
    my_addr.sin_addr.s_addr = htonl(INADDR_ANY);  
    bind(sockfd, (struct sockaddr *)&my_addr, sizeof(my_addr)); 
	listen(sockfd, 10); 
	
	return sockfd;
}