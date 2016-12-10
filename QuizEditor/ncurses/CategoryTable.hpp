#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <menu.h>

#include <sstream>
#include <iomanip>
#include <string>
#include <map>

#include "ConfirmDialog.hpp"

using namespace std;

class CategoryTable {

public:
    CategoryTable(void);
    ~CategoryTable(void);

    void run(void);
    int popup(void);

private:
    void del(int);
    void insert(void);
    void update(int);
    void update_items(bool);

    ConfirmDialog dialog;
    WINDOW *subwin, *window;
    MENU *menu;
    ITEM **items;
    map<string, string> rows;
    string header;
    int max_height, max_width;
    unsigned int row_count;
};
