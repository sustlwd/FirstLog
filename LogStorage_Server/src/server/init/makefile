all:init_socket.o init_server.o Openfile.o communication.o
init_socket.o : init_socket.c
	gcc -c init_socket.c -o init_socket.o -I ../../../include
	mv init_socket.o ../../../obj/server/ 
init_server.o:init_server.c
	gcc -c init_server.c -o init_server.o -I ../../../include/ 
	mv init_server.o ../../../obj/server/ 
Openfile.o:Openfile.c
	gcc -c Openfile.c -o Openfile.o -I ../../../include
	mv Openfile.o ../../../obj/server/ 
communication.o:communication.c
	gcc -c communication.c -o communication.o -I ../../../include
	mv communication.o ../../../obj/server/ 
	
