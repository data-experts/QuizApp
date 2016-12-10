#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <menu.h>

#include <functional>
#include <vector>
#include <map>

using namespace std;

typedef function<bool (const unsigned int&)> MenuInputHandler;

class MenuWindow {

public:
    MenuWindow(const vector<map<const char*, const char*>>&);
    void open(const MenuInputHandler&);
    void close(void);
    ~MenuWindow(void);

private:
    void draw(void);

    MENU *menu;
    ITEM **items;
    WINDOW *window, *subwin;
    unsigned int item_count;
};
