#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <menu.h>

class ConfirmDialog {

public:
    ConfirmDialog(void);
    ~ConfirmDialog(void);
    bool prompt(void);

private:
    MENU *menu;
    ITEM **items;
    WINDOW *window, *subwin;
};
