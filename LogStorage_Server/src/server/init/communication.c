#include "Server.h"

void communication()
{
	int maxi = 0,i , ret;
	while(1)  
    {  
        // 监视并等待多个文件（标准输入，tcp套接字）描述符的属性变化（是否可读）    
        // 没有属性变化，这个函数会阻塞，直到有变化才往下执行，这里没有设置超时     
        ret = epoll_wait(epfd, &wait_event, maxi+1, -1);   
          
        //6.1监测sockfd(监听套接字)是否存在连接  
        if(( sockfd == wait_event.data.fd )     
            && ( EPOLLIN == wait_event.events & EPOLLIN ) )  
        {  
            struct sockaddr_in cli_addr;  
            int clilen = sizeof(cli_addr);  
              
            //6.1.1 从tcp完成连接中提取客户端  
            int connfd = accept(sockfd, (struct sockaddr *)&cli_addr, &clilen);  
             printf("accept ret = %d\n",connfd); 
            //6.1.2 将提取到的connfd放入fd数组中，以便下面轮询客户端套接字  
            for(i=2; i<OPEN_MAX; i++)  
            {  
                if(fd[i] < 0)  
                {  
                    fd[i] = connfd;  
                    event.data.fd = connfd; //监听套接字    
                    event.events = EPOLLIN; // 表示对应的文件描述符可以读  
                      
                    //6.1.3.事件注册函数，将监听套接字描述符 connfd 加入监听事件    
                    ret = epoll_ctl(epfd, EPOLL_CTL_ADD, connfd, &event);    
                    if(-1 == ret){    
                        perror("epoll_ctl");    
                        return;    
                    }   
                   
                    break;  
                }  
            }  
              
            //6.1.4 maxi更新  
            if(i > maxi)  
                maxi = i;  
                  
            //6.1.5 如果没有就绪的描述符，就继续epoll监测，否则继续向下看  
            if(--ret <= 0)  
                continue;  
        }
        //6.2继续响应就绪的描述符  
        for(i=1; i<=maxi; i++)  
        {  
            if(fd[i] < 0)  
                continue;  
            if(( fd[i] == wait_event.data.fd )     
            && ( EPOLLIN == wait_event.events & (EPOLLIN|EPOLLERR) ))  
            { 	
				//7.1 监听标准输入，如果终端有输入将其存入command
				if(0 == fd[i])   
				{
					gets(command);
					puts("debug2");
					puts(command);
				}
				//7.2 监听client端，与client端完成数据收发
				else
				{
					int len = 0;  
					int SendRet = 0;
					char buf[4096] = "";  
					char Recv[] = "recv successfully\n";
					if((len = recv(fd[i], buf, sizeof(buf), 0)) < 0)  
					  {  
							printf("first data from client:\n");
							puts(buf);
						if(errno == ECONNRESET)//tcp连接超时、RST  
						{  
							close(fd[i]);  
							fd[i] = -1;  
							printf("recv error!\n");
						}  
						else  
						{
							perror("read error:");
						}							
					  }		  
					else if(len == 0)//客户端关闭连接  
					{  
					printf("client shutdown!\n");
                    close(fd[i]);  
                    fd[i] = -1;  
					}
					else
					{
						//printf("----%d\n",len);
						//7.2.1  client请求发送log到server
						if(0 == strncmp("logstart" , buf , 8))
						{
							puts(buf);
							write(fd_info , buf+8 , 12);
							memset(buf , 0 , sizeof(buf));
						}
						//else if(0 == strcmp("client shutdown" , buf))
						//{
						//	close(fd[i]);  
						//	fd[i] = -1;  
						//	printf("client send over\n");
						//}
						else
						{
							//7.2.2  client端将多个log文件传输到server端，文件数据用finish标志间隔开
							if(0 == strncmp("logfinish" , buf , 9))
							{
								//puts(buf);
								if(index_txt == 3)
								{
									index_txt = 0;
								}
								else
								{
									++index_txt;
								}
							}
							else
							{
								write(fd_log[index_txt] , buf , len);
							}
							memset(buf , 0 , sizeof(buf));
						}
						//printf(".......%d\n",(int)strlen(command));
						if(0 < strlen(command))  //7.2.3 如果终端有输入，将内容发送到client
						{
							puts("---command---");
							puts(command);
							strcat(command , "\n");
							//char Msg[] = "command\n";
							send(fd[i] , command , strlen(command) , 0);
							
							puts("Msg has been sent to client!");
							memset(command , 0 , sizeof(command));
							//7.2.4  获取client执行command的结果，输出在终端
							while(1) //执行结果数据太长，循环分段接收
							{
								len = recv(fd[i], buf, sizeof(buf), 0);
								if(len < 0)
								{
									puts("command recv error!");
									break;
								}
								puts(buf);
								//printf("++++++++++++++%d+++++++++++++\n",(int)strlen(buf));
								if((int)strlen(buf) < 2048) //接收最后一段，结束循环
								{
									memset(buf , 0 , sizeof(buf));
									break;
								}
								else
								{
									memset(buf , 0 , sizeof(buf));
								}
							}
							send(fd[i] , Recv , strlen(Recv) , 0);
						}
						else  //7.3  server收到数据回复	
						{
							send(fd[i] , Recv , strlen(Recv) , 0);
						}
							usleep(100);
					}
				}
			}
        }  
    }
}
