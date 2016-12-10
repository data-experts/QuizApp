#include "Curses.hpp"
#include "MenuWindow.hpp"
#include "CategoryTable.hpp"
#include "QuestionTable.hpp"
#include "DBConnection.hpp"

int main(int argc, char** argv) {
    DBConnection* connection = new DBConnection();
    Curses* curses = new Curses();

    MenuWindow menu({
        {{ "Questions  ", "Maintain questions" }},
        {{ "Categories ", "Maintain categories" }},
        {{ "Exit       ", "Exit program" }}
    });

    bool running = true;

    while (running) {
        menu.open([&] (const unsigned int &index) {
            switch (index) {
                case 0: {
                    menu.close();
                    QuestionTable questions;
                    questions.run();
                    return false;
                }
                case 1: {
                    menu.close();
                    CategoryTable categories;
                    categories.run();
                    return false;
                }
                case 2:
                    menu.close();
                    running = false;
                    return false;
            }
            return true;
        });
    }

    delete curses;
    delete connection;
    return 0;
}
