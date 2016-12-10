#pragma once

#include <stddef.h> // ncurses fix on some distros
#include <cursesw.h>
#include <form.h>
#include <menu.h>

class QuestionForm {

public:
    QuestionForm(char*, char*[], int*, bool);
    ~QuestionForm(void);

private:
    void readfield(const FIELD*, char*);

    WINDOW *form_subwin, *window, *menu_subwin;
    FIELD** fields;
    FORM* form;
    MENU *menu;
    ITEM **items;
};
