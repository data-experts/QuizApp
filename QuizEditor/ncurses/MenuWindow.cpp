#include "MenuWindow.hpp"

MenuWindow::MenuWindow(const vector<map<const char*, const char*>> &items) {
    int rows, cols, max_width, max_height, width, height;

    item_count = items.size();
    this->items = new ITEM* [item_count + 1]();

    for (unsigned int i = 0; i < item_count; i++) {
        this->items[i] = new_item(items[i].begin()->first, items[i].begin()->second);
    }

    menu = new_menu(this->items);
    set_menu_mark(menu, " > ");

    scale_menu(menu, &rows, &cols);
    getmaxyx(stdscr, max_height, max_width);

    width = cols + 5;
    height = rows + 4;

    window = newwin(height, width, (max_height - height) * .5, (max_width - width) * .5);
    subwin = derwin(window, rows, cols, 2, 1);

    wbkgd(window, COLOR_PAIR(3));
    wbkgd(subwin, COLOR_PAIR(3));

    set_menu_win(menu, window);
    set_menu_sub(menu, subwin);
}

MenuWindow::~MenuWindow(void) {
    unpost_menu(menu);
    delwin(subwin);
    delwin(window);
    free_menu(menu);
    for (unsigned int i = 0; i < item_count; i++) {
        free_item(items[i]);
    }
    delete[] items;
}

void MenuWindow::open(const MenuInputHandler &handler) {
    int input;
    bool running = true;

    draw();
    post_menu(menu);
    refresh();
    wrefresh(window);

    while (running && (input = getch())) {
        switch (input) {
            case KEY_DOWN:
                menu_driver(menu, REQ_DOWN_ITEM);
                wrefresh(window);
                break;
            case KEY_UP:
                menu_driver(menu, REQ_UP_ITEM);
                wrefresh(window);
                break;
            case 0xA:
                running = handler(item_index(current_item(menu)));
        }
    }
}

void MenuWindow::draw(void) {
    box(window, 0, 0);
    box(subwin, 0, 0);

    mvwaddstr(window, 0, 3, " QuizEditor ");
}

void MenuWindow::close(void) {
    unpost_menu(menu);
    clear();
}
