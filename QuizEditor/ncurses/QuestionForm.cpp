#include "QuestionForm.hpp"
#include "CategoryTable.hpp"

#include <cstring>
#include <string>

using namespace std;

QuestionForm::QuestionForm(char* question, char* options[], int* cid, bool is_insert_mode) {
    int rows, cols, max_width, max_height, width, height;

    fields = new FIELD* [7]();
    items = new ITEM* [3]();

    // QUESTION field
    fields[0] = new_field(1, 41, 0, 0, 0, 0);
    set_field_fore(fields[0], COLOR_PAIR(9));
    set_field_back(fields[0], COLOR_PAIR(9));
    field_opts_off(fields[0], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[0], 255);
    set_field_buffer(fields[0], 0, question);

    // ANSWER field
    fields[1] = new_field(1, 20, 2, 0, 0, 0);
    set_field_fore(fields[1], COLOR_PAIR(9));
    set_field_back(fields[1], COLOR_PAIR(9));
    field_opts_off(fields[1], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[1], 255);
    set_field_buffer(fields[1], 0, options[0]);

    // SUGGESTION1 field
    fields[2] = new_field(1, 20, 2, 21, 0, 0);
    set_field_fore(fields[2], COLOR_PAIR(10));
    set_field_back(fields[2], COLOR_PAIR(10));
    field_opts_off(fields[2], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[2], 255);
    set_field_buffer(fields[2], 0, options[1]);

    // SUGGESTION2 field
    fields[3] = new_field(1, 20, 4, 0, 0, 0);
    set_field_fore(fields[3], COLOR_PAIR(10));
    set_field_back(fields[3], COLOR_PAIR(10));
    field_opts_off(fields[3], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[3], 255);
    set_field_buffer(fields[3], 0, options[2]);

    // SUGGESTION3 field
    fields[4] = new_field(1, 20, 4, 21, 0, 0);
    set_field_fore(fields[4], COLOR_PAIR(10));
    set_field_back(fields[4], COLOR_PAIR(10));
    field_opts_off(fields[4], O_STATIC);
    field_opts_off(fields[0], O_AUTOSKIP);
    set_max_field(fields[4], 255);
    set_field_buffer(fields[4], 0, options[3]);

    // CID field
    fields[5] = new_field(1, 4, 6, 10, 0, 0);
    set_field_fore(fields[5], COLOR_PAIR(9));
    set_field_back(fields[5], COLOR_PAIR(9));
    field_opts_off(fields[5], O_AUTOSKIP);
    set_field_buffer(fields[5], 0, *cid <= 0 ? "" : to_string(*cid).c_str());

    if (is_insert_mode) {
        items[0] = new_item("  CREATE  ", "");
    } else {
        items[0] = new_item("   EDIT   ", "");
    }
    items[1] = new_item("  CANCEL  ", "");

    form = new_form(fields);

    menu = new_menu(items);
    set_menu_fore(menu, COLOR_PAIR(8));
    set_menu_format(menu, 1, 2);
    set_menu_mark(menu, "");

    scale_form(form, &rows, &cols);
    getmaxyx(stdscr, max_height, max_width);

    width = cols + 4;
    height = rows + 3;

    window = newwin(height, width, (max_height - height) * .5, (max_width - width) * .5);
    form_subwin = derwin(window, rows, cols, 2, 2);

    scale_menu(menu, &rows, &cols);

    width = cols + 4;
    height = rows + 3;

    menu_subwin = derwin(window, rows, cols, 8, 22);

    wbkgd(window, COLOR_PAIR(3));
    wbkgd(form_subwin, COLOR_PAIR(3));
    wbkgd(menu_subwin, COLOR_PAIR(3));

    set_form_win(form, window);
    set_form_sub(form, form_subwin);

    set_menu_win(menu, window);
    set_menu_sub(menu, menu_subwin);

    box(window, 0, 0);
    if (is_insert_mode) {
        mvwaddstr(window, 0, 1, " Create Question ");
    } else {
        mvwaddstr(window, 0, 1, " Edit Question ");
    }
    post_form(form);
    wattron(window, COLOR_PAIR(8));
    mvwaddstr(window, 8, 2, "Category:");
    wattroff(window, COLOR_PAIR(8));
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
                if (form_focus && field_index(current_field(form)) == 5) {
                    form_focus = false;
                    set_menu_fore(menu, COLOR_PAIR(8) | A_REVERSE);
                    menu_driver(menu, REQ_FIRST_ITEM);
                    curs_set(0);
                } else if (!form_focus && item_index(current_item(menu)) == 0) {
                    menu_driver(menu, REQ_LAST_ITEM);
                } else if (!form_focus && item_index(current_item(menu)) == 1) {
                    form_focus = true;
                    set_menu_fore(menu, COLOR_PAIR(8));
                    form_driver(form, REQ_FIRST_FIELD);
                    form_driver(form, REQ_END_FIELD);
                    curs_set(1);
                } else {
                    form_driver(form, REQ_NEXT_FIELD);
                    form_driver(form, REQ_END_FIELD);
                }
                break;
            case KEY_BTAB:
                if (form_focus && field_index(current_field(form)) == 0) {
                    form_focus = false;
                    set_menu_fore(menu, COLOR_PAIR(8) | A_REVERSE);
                    menu_driver(menu, REQ_LAST_ITEM);
                    curs_set(0);
                } else if (!form_focus && item_index(current_item(menu)) == 1) {
                    menu_driver(menu, REQ_FIRST_ITEM);
                } else if (!form_focus && item_index(current_item(menu)) == 0) {
                    form_focus = true;
                    set_menu_fore(menu, COLOR_PAIR(8));
                    form_driver(form, REQ_LAST_FIELD);
                    form_driver(form, REQ_END_FIELD);
                    curs_set(1);
                } else {
                    form_driver(form, REQ_PREV_FIELD);
                    form_driver(form, REQ_END_FIELD);
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
                        readfield(fields[0], question);
                        readfield(fields[1], options[0]);
                        readfield(fields[2], options[1]);
                        readfield(fields[3], options[2]);
                        readfield(fields[4], options[3]);
                        char cid_string[1021] = { 0 };
                        readfield(fields[5], cid_string);
                        *cid = atoi(cid_string);
                    } else {
                        question[0] = 0;
                    }
                    return;
                } else if (field_index(current_field(form)) == 5) {
                    curs_set(0);
                    CategoryTable* categories = new CategoryTable();
                    int id = categories->popup();
                    delete categories;
                    if (id != 0) {
                        set_field_buffer(fields[5], 0, to_string(id).c_str());
                        form_driver(form, REQ_END_FIELD);
                    }
                    clear();
                    curs_set(1);
                    touchwin(window);
                    refresh();
                }
            default: /* Feldeingabe */ 
                if (form_focus) {
                    form_driver(form, input);
                }
        }
        wrefresh(window);
    }
}

void QuestionForm::readfield(const FIELD* field, char* buffer) {
    strncpy(buffer, field_buffer(field, 0), 1020);
    buffer[1020] = 0;
    string temp(buffer);
    temp.erase(0, temp.find_first_not_of(" "));
    temp.erase(temp.find_last_not_of(" ") + 1);
    strncpy(buffer, temp.c_str(), 1020);
}

QuestionForm::~QuestionForm(void) {
    curs_set(0);
    unpost_menu(menu);
    unpost_form(form);
    delwin(form_subwin);
    delwin(menu_subwin);
    delwin(window);
    free_form(form);
    free_menu(menu);
    free_field(fields[0]);
    free_field(fields[1]);
    free_field(fields[2]);
    free_field(fields[3]);
    free_field(fields[4]);
    free_field(fields[5]);
    free_item(items[0]);
    free_item(items[1]);
    delete[] fields;
    delete[] items;
}
