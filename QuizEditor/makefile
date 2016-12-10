CC=g++
CFLAGS=-std=c++11 -Wall
TARGET=QuizEditor
INCLUDE=/usr/include/postgresql
LIB=/usr/lib/psql

all: $(TARGET)

clean:
	rm -f *.o *.c $(TARGET)

$(TARGET): main.o
	$(CC) $(CFLAGS) -o $(TARGET) main.o -L$(LIB) -lecpg

main.o: main.c
	$(CC) $(CFLAGS) -c main.c -I$(INCLUDE)

main.c: main.cpp
	ecpg main.cpp
