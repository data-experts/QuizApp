#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <locale.h>

class Curses {

public:
    Curses(void);
    ~Curses(void);
};
