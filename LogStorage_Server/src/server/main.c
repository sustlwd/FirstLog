#include "Server.h"
  
int main(int argc, char *argv[])  
{  
	//创建socket对象
	SocketInit();
	//初始化服务器配置
    ServerInit();
	//文件IO操作
	OpenFile();
    //对已连接的客户端的数据处理  
	communication();
    return 0;  
}  
