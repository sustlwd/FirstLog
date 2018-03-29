# include "Server.h"

void OpenFile()
{
	fd_info = open("./info" , O_RDWR | O_CREAT , 0666);
	fd_log[0] = open("./logcat.txt" , O_RDWR | O_CREAT , 0666);
	fd_log[1] = open("./trace.txt" , O_RDWR | O_CREAT , 0666);
	fd_log[2] = open("./tomb.txt" , O_RDWR | O_CREAT , 0666);
	fd_log[3] = open("./event.txt" , O_RDWR | O_CREAT , 0666);
	printf("opened txt file\n ");
}