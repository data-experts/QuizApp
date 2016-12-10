#include "ConfirmDialog.hpp"

ConfirmDialog::ConfirmDialog(void) {
    int rows, cols, max_width, max_height, width, height;

    items = new ITEM* [3]();

    items[0] = new_item("  DELETE  ", "");
    items[1] = new_item("  CANCEL  ", "");

    menu = new_menu(items);
    set_menu_format(menu, 1, 2);
    set_menu_mark(menu, "");

    scale_menu(menu, &rows, &cols);
    getmaxyx(stdscr, max_height, max_width);

    width = cols + 4;
    height = rows + 3;

    window = newwin(height, width, (max_height - height) * .5, (max_width - width) * .5);
    subwin = derwin(window, rows, cols, 2, 2);

    wbkgd(window, COLOR_PAIR(3));
    wbkgd(subwin, COLOR_PAIR(3));

    set_menu_win(menu, window);
    set_menu_sub(menu, subwin);
}

ConfirmDialog::~ConfirmDialog(void) {
    unpost_menu(menu);
    delwin(subwin);
    delwin(window);
    free_menu(menu);
    free_item(items[0]);
    free_item(items[1]);
    delete[] items;
}

bool ConfirmDialog::prompt(void) {
    int input;

    box(window, 0, 0);
    mvwaddstr(window, 0, 4, " Delete Category ");
    post_menu(menu);
    menu_driver(menu, REQ_LAST_ITEM);
    refresh();
    wrefresh(window);

    while (true) {
        input = getch();
        switch (input) {
            case 0x09: // tabulator
            case KEY_BTAB:
                if (item_index(current_item(menu)) == 1) {
                    menu_driver(menu, REQ_LEFT_ITEM);
                } else {
                    menu_driver(menu, REQ_RIGHT_ITEM);
                }
                break;
            case KEY_LEFT:
                menu_driver(menu, REQ_LEFT_ITEM);
                break;
            case KEY_RIGHT:
                menu_driver(menu, REQ_RIGHT_ITEM);
                break;
            case 0x0A:
                unpost_menu(menu);
                return item_index(current_item(menu)) == 0;
        }
        wrefresh(window);
    }
}
