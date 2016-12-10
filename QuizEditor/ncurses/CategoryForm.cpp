#include "CategoryForm.hpp"

#include <cstring>
#include <string>

using namespace std;

CategoryForm::CategoryForm(char* name, bool is_insert_mode) {
    int rows, cols, max_width, max_height, width, height;

    fields = new FIELD* [2]();
    items = new ITEM* [3]();

    fields[0] = new_field(1, 30, 0, 0, 0, 0);
    if (is_insert_mode) {
        items[0] = new_item("  CREATE  ", "");
    } else {
        items[0] = new_item("   EDIT   ", "");
    }
    items[1] = new_item("  CANCEL  ", "");

    set_field_fore(fields[0], COLOR_PAIR(9));
    set_field_back(fields[0], COLOR_PAIR(9));
    field_opts_off(fields[0], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[0], 255);
    set_field_buffer(fields[0], 0, name);

    form = new_form(fields);

    menu = new_menu(items);
    set_menu_fore(menu, COLOR_PAIR(8));
    set_menu_format(menu, 1, 2);
    set_menu_mark(menu, "");

    scale_form(form, &rows, &cols);
    getmaxyx(stdscr, max_height, max_width);

    width = cols + 4;
    height = rows + 5;

    window = newwin(height, width, (max_height - height) * .5, (max_width - width) * .5);
    form_subwin = derwin(window, rows, cols, 2, 2);

    scale_menu(menu, &rows, &cols);

    width = cols + 4;
    height = rows + 3;

    menu_subwin = derwin(window, rows, cols, 4, 11);

    wbkgd(window, COLOR_PAIR(3));
    wbkgd(form_subwin, COLOR_PAIR(3));
    wbkgd(menu_subwin, COLOR_PAIR(3));

    set_form_win(form, window);
    set_form_sub(form, form_subwin);

    set_menu_win(menu, window);
    set_menu_sub(menu, menu_subwin);

    box(window, 0, 0);
    if (is_insert_mode) {
        mvwaddstr(window, 0, 1, " Create Category ");
    } else {
        mvwaddstr(window, 0, 1, " Edit Category ");
    }
    post_form(form);
    post_menu(menu);
    menu_driver(menu, REQ_LAST_ITEM);
    form_driver(form, REQ_END_FIELD);
    refresh();
    wrefresh(window);
    curs_set(1);

    int input;
    bool form_focus = true;
    while(true) {
        input = getch();
        switch(input) {
            case KEY_RIGHT:
                if (form_focus) {
                    form_driver(form, REQ_NEXT_CHAR);
                } else {
                    menu_driver(menu, REQ_LAST_ITEM);
                }
                break;
            case KEY_LEFT:
                if (form_focus) {
                    form_driver(form, REQ_PREV_CHAR);
                } else {
                    menu_driver(menu, REQ_FIRST_ITEM);
                }
                break;
            case KEY_BACKSPACE:
                if (form_focus) {
                    form_driver(form, REQ_PREV_CHAR);
                    form_driver(form, REQ_DEL_CHAR);
                }
                break;
            case 0x09: // tabulator
                if (form_focus) {
                    form_focus = false;
                    set_menu_fore(menu, COLOR_PAIR(8) | A_REVERSE);
                    menu_driver(menu, REQ_FIRST_ITEM);
                    curs_set(0);
                } else if (item_index(current_item(menu)) == 0) {
                    menu_driver(menu, REQ_LAST_ITEM);
                } else {
                    form_focus = true;
                    set_menu_fore(menu, COLOR_PAIR(8));
                    form_driver(form, REQ_NEXT_FIELD);
                    form_driver(form, REQ_END_FIELD);
                    curs_set(1);
                }
                break;
            case KEY_BTAB:
                if (form_focus) {
                    form_focus = false;
                    set_menu_fore(menu, COLOR_PAIR(8) | A_REVERSE);
                    menu_driver(menu, REQ_LAST_ITEM);
                    curs_set(0);
                } else if (item_index(current_item(menu)) == 1) {
                    menu_driver(menu, REQ_FIRST_ITEM);
                } else {
                    form_focus = true;
                    set_menu_fore(menu, COLOR_PAIR(8));
                    form_driver(form, REQ_LAST_FIELD);
                    form_driver(form, REQ_END_FIELD);
                    curs_set(1);
                }
                break;
            case KEY_DC:
                if (form_focus) {
                    form_driver(form, REQ_DEL_CHAR);
                }
                break;
            case KEY_END:
                form_driver(form, REQ_END_FIELD);
                break;
            case KEY_HOME:
                form_driver(form, REQ_BEG_FIELD);
                break;
            case 0x0A:
                if (!form_focus) {
                    if (item_index(current_item(menu)) == 0) {
                        form_driver(form, REQ_VALIDATION);
                        strncpy(name, field_buffer(fields[0], 0), 1020);
                        name[1020] = 0;
                        string temp(name);
                        temp.erase(0, temp.find_first_not_of(" "));
                        temp.erase(temp.find_last_not_of(" ") + 1);
                        strncpy(name, temp.c_str(), 1020);
                    } else {
                        name[0] = 0;
                    }
                    return;
                }
            default: /* Feldeingabe */
                if (form_focus) {
                    form_driver(form, input);
                }
        }
        wrefresh(window);
    }
}

CategoryForm::~CategoryForm(void) {
    curs_set(0);
    unpost_menu(menu);
    unpost_form(form);
    delwin(form_subwin);
    delwin(menu_subwin);
    delwin(window);
    free_form(form);
    free_menu(menu);
    free_field(fields[0]);
    free_item(items[0]);
    free_item(items[1]);
    delete[] fields;
    delete[] items;
}
