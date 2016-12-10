CC=g++
CFLAGS=-std=c++11 -Wall
TARGET=QuizEditor
LIBS=-l:libmenuw.a -l:libformw.a -l:libncursesw.a -l:libtinfo.a -l:libgpm.a -lecpg
INCLUDE=/usr/include/postgresql
LIB=/usr/lib/psql

all:
	ecpg -r no_indicator *.pgc
	$(CC) $(CFLAGS) *.cpp *.c -o $(TARGET) $(LIBS) -L$(LIB) -I$(INCLUDE)

clean:
	rm -f *.c $(TARGET)
