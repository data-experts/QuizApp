#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <form.h>
#include <menu.h>

class CategoryForm {

public:
    CategoryForm(char*, bool);
    ~CategoryForm(void);

private:
    WINDOW *form_subwin, *window, *menu_subwin;
    FIELD** fields;
    FORM* form;
    MENU *menu;
    ITEM **items;
};
