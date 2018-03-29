all:main.o
	make -C init
main.o:main.c
	gcc -c main.c -o main.o -I ../../include/ 
	mv main.o ../../obj/server/ 
