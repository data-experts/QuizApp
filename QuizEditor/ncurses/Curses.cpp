#include "Curses.hpp"

Curses::Curses(void) {
    setlocale(LC_ALL, "");

    initscr();

    start_color();
    use_default_colors();

    init_pair(1, COLOR_BLACK, -1);
    init_pair(2, COLOR_RED, -1);
    init_pair(3, COLOR_GREEN, -1);
    init_pair(4, COLOR_YELLOW, -1);
    init_pair(5, COLOR_BLUE, -1);
    init_pair(6, COLOR_MAGENTA, -1);
    init_pair(7, COLOR_CYAN, -1);
    init_pair(8, COLOR_WHITE, -1);
    init_pair(9, COLOR_BLACK, COLOR_GREEN);
    init_pair(10, COLOR_BLACK, COLOR_RED);

    nl();
    raw();
    noecho();
    curs_set(0);

    keypad(stdscr, TRUE);

    clear();
    refresh();
}

Curses::~Curses(void) {
    endwin();
}
