#include <functional>
#include <iostream>
#include <iomanip>
#include <vector>
#include <string>
#include <limits>
#include <map>

using namespace std;

// ANSI color constants
const char* RESET   = "\033[0m";
const char* BLACK   = "\033[30m";
const char* RED     = "\033[31m";
const char* GREEN   = "\033[32m";
const char* YELLOW  = "\033[33m";
const char* BLUE    = "\033[34m";
const char* MAGENTA = "\033[35m";
const char* CYAN    = "\033[36m";
const char* WHITE   = "\033[37m";

// ANSI color constants (bold version)
const char* BOLDBLACK   = "\033[1m\033[30m";
const char* BOLDRED     = "\033[1m\033[31m";
const char* BOLDGREEN   = "\033[1m\033[32m";
const char* BOLDYELLOW  = "\033[1m\033[33m";
const char* BOLDBLUE    = "\033[1m\033[34m";
const char* BOLDMAGENTA = "\033[1m\033[35m";
const char* BOLDCYAN    = "\033[1m\033[36m";
const char* BOLDWHITE   = "\033[1m\033[37m";

typedef function<bool (const string&)> MenuInputHandler;

EXEC SQL BEGIN DECLARE SECTION;

	const int MAX_FETCH_SIZE = 5;

	// Table record of QUESTIONS
	typedef struct {
		int ID;
		int CID;
		char QUESTION[256];
		char CHOICE1[256];
		char CHOICE2[256];
		char CHOICE3[256];
		char CHOICE4[256];
		int ANSWER;
	} tQUESTION;

	// Table record of CATEGORIES
	typedef struct {
		int ID;
		char NAME[256];
	} tCATEGORY;

EXEC SQL END DECLARE SECTION;

// function declarations
void connect_db(void);
void disconnect_db(void);
void open_list_cursor(void);
void close_list_cursor(void);
void create_category(void);
void create_question(void);
void delete_category(void);
void delete_question(void);
void modify_category(void);
void modify_question(void);
void list_questions(void);
void list_categories(void);
void list_questions(const bool);
void list_categories(const bool);
void print_help(const string&, const vector<map<string, string>>&);
void print_main_help(void);
void print_list_help(void);
void print_common_help(void);
void print_status_switch(const string&);
void print_exit_msg(const string&);
void print_user_prompt(const string&, const MenuInputHandler&);
string get_user_input(void);

// TODO: Implement update mode
// TODO: Catch the deletion of -1 category
// TODO: In list context -> show if start or end is reached
int main(int argc, char** argv) {
	connect_db();

	print_user_prompt("", [&] (const string &input) {
		if (input == "new") {

			print_status_switch("new");

			print_user_prompt("new", [&] (const string &input) {
				if (input == "question") {
					create_question();
				} else if (input == "category") {
					create_category();
				} else if (input == "exit") {
					print_exit_msg("new");
					return false;
				} else { 
					print_common_help();
				}
				return true;
			});

		} else if (input == "delete") {

			print_status_switch("delete");

			print_user_prompt("delete", [&] (const string &input) {
				if (input == "question") {
					delete_question();
				} else if (input == "category") {
					delete_category();
				} else if (input == "exit") {
					print_exit_msg("delete");
					return false;
				} else { 
					print_common_help();
				}
				return true;
			});

		} else if (input == "modify") {

			print_status_switch("modify");

			print_user_prompt("modify", [&] (const string &input) {
				if (input == "question") {
					modify_question();
				} else if (input == "category") {
					modify_category();
				} else if (input == "exit") {
					print_exit_msg("modify");
					return false;
				} else { 
					//print_common_help();
				}
				return true;
			});

		} else if (input == "list") {

			string mode; // are we currently in question or category mode?

			print_status_switch("list");
			open_list_cursor();

			print_user_prompt("list", [&] (const string &input) {
				if (input == "question") {
					list_questions();
					mode = "question";
				} else if (input == "category") {
					list_categories();
					mode = "category";
				} else if (input == "next" || input == "prev") {
					if (mode == "question") {
						list_questions(input == "next");
					} else if (mode == "category") {
						list_categories(input == "next");
					}
				} else if (input == "exit") {
					print_exit_msg("list");
					return false;
				} else { 
					print_list_help();
				}
				return true;
			});

			close_list_cursor();

		} else if (input == "exit") {
			return false;
		} else {
			print_main_help();
		}
		return true;
	});

	disconnect_db();
	return 0;
}

// Starts a new default db connection and set some SQL behavior variables
void connect_db(void) {
	cout << BLUE << "Connecting to target database: " << RESET << flush;
	EXEC SQL CONNECT TO 'mydbname@my.domain.de' USER mydbuser IDENTIFIED BY mydbpassword;
	EXEC SQL WHENEVER SQLERROR SQLPRINT;
	EXEC SQL WHENEVER SQLWARNING SQLPRINT;
	cout << BOLDGREEN << "CONNECTED\n\n" << RESET << flush;
}

// Closes all opened db connections
void disconnect_db(void) {
	cout << BLUE << "\nDisconnecting from target database: " << RESET << flush;
	EXEC SQL DISCONNECT ALL;
	cout << BOLDRED << "DISCONNECTED\n" << RESET << flush;
}

// Prints the CLI prompt to stdout with a given context name
// The given handler function will execute every time until it returns a false value
void print_user_prompt(const string &context, const MenuInputHandler& handler) {
	bool status = true;
	string ctx(context);

	if (ctx.length() > 1) { ctx = ":" + ctx; }

	while (status) {
		cout << BOLDWHITE << "QuizEditor" << ctx << "> " << RESET << flush;
		status = handler(get_user_input());
	}
}

// Handles the user input in print_user_prompt call
string get_user_input(void) {
	string input;
	cout << BLUE; getline(cin, input); cout << RESET;
	return input;
}

// Opens the needed cursor for listing categories and questions
void open_list_cursor(void) {
	EXEC SQL DECLARE category_cur SCROLL CURSOR FOR SELECT * FROM "CATEGORIES" WHERE "ID"<>-1 ORDER BY "ID" ASC;
	EXEC SQL DECLARE question_cur SCROLL CURSOR FOR SELECT * FROM "QUESTIONS" ORDER BY "ID" ASC;
	EXEC SQL OPEN category_cur;
	EXEC SQL OPEN question_cur;
}

// Closes the opened cursor from open_list_cursor function
void close_list_cursor(void) {
	EXEC SQL CLOSE category_cur;
	EXEC SQL CLOSE question_cur;
}

// Lists the first page of QUESTIONS table
void list_questions(void) {
	EXEC SQL MOVE BACKWARD ALL question_cur;
	list_questions(true);
}

// Lists the first page of CATEGORIES table
void list_categories(void) {
	EXEC SQL MOVE BACKWARD ALL category_cur;
	list_categories(true);
}

// Lists the next or previous page of QUESTIONS table by a cursor
void list_questions(const bool forward) {
	EXEC SQL BEGIN DECLARE SECTION;
		tQUESTION rec[MAX_FETCH_SIZE] = { 0 };
	EXEC SQL END DECLARE SECTION;

	if (forward) {
		EXEC SQL FETCH FORWARD :MAX_FETCH_SIZE FROM question_cur INTO :rec;
		for (int i = 0; i < MAX_FETCH_SIZE; i++) {
			if (rec[i].ID == 0) break;
			cout << setw(4) << rec[i].ID << " | " << setw(4) << rec[i].CID  << " | " << rec[i].QUESTION
				<< " | " << rec[i].CHOICE1
				<< " | " << rec[i].CHOICE2
				<< " | " << rec[i].CHOICE3
				<< " | " << rec[i].CHOICE4
				<< " | " << rec[i].ANSWER << "\n";
		}
	} else {
		EXEC SQL FETCH BACKWARD :MAX_FETCH_SIZE FROM question_cur INTO :rec;
		for (int i = MAX_FETCH_SIZE - 1; i >= 0; i--) {
			if (rec[i].ID == 0) continue;
			cout << setw(4) << rec[i].ID << " | " << setw(4) << rec[i].CID  << " | " << rec[i].QUESTION
				<< " | " << rec[i].CHOICE1
				<< " | " << rec[i].CHOICE2
				<< " | " << rec[i].CHOICE3
				<< " | " << rec[i].CHOICE4
				<< " | " << rec[i].ANSWER << "\n";
		}
	}
}

// Lists the next or previous page of CATEGORIES table by a cursor
void list_categories(const bool forward) {
	EXEC SQL BEGIN DECLARE SECTION;
		tCATEGORY rec[MAX_FETCH_SIZE] = { 0 };
	EXEC SQL END DECLARE SECTION;

	if (forward) {
		EXEC SQL FETCH FORWARD :MAX_FETCH_SIZE FROM category_cur INTO :rec;
		for (int i = 0; i < MAX_FETCH_SIZE; i++) {
			if (rec[i].ID == 0) break;
			cout << setw(4) << rec[i].ID << " | " << rec[i].NAME << "\n";
		}
	} else {
		EXEC SQL FETCH BACKWARD :MAX_FETCH_SIZE FROM category_cur INTO :rec;
		for (int i = MAX_FETCH_SIZE - 1; i >= 0; i--) {
			if (rec[i].ID == 0) continue;
			cout << setw(4) << rec[i].ID << " | " << rec[i].NAME << "\n";
		}
	}
}

// Executes a query to delete a given question by id from the current database connection
void delete_question(void) {
	EXEC SQL BEGIN DECLARE SECTION;
		int pID;
	EXEC SQL END DECLARE SECTION;

	while (cout << "Which question ID? " && !(cin >> pID)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	EXEC SQL DELETE FROM "QUESTIONS" WHERE "ID"=:pID;
	EXEC SQL COMMIT;
}

// Executes a query to delete a given category by id from the current database connection
void delete_category(void) {
	EXEC SQL BEGIN DECLARE SECTION;
		int pID;
	EXEC SQL END DECLARE SECTION;

	while (cout << "Which category ID? " && !(cin >> pID)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	EXEC SQL DELETE FROM "CATEGORIES" WHERE "ID"=:pID;
	EXEC SQL COMMIT;
}

// Executes a query to modify an existing question by id from the current database connection
void modify_question(void) {
	EXEC SQL BEGIN DECLARE SECTION;
		tQUESTION rec = { 0 };
	EXEC SQL END DECLARE SECTION;

	while (cout << "Which question ID? " && !(cin >> rec.ID)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	EXEC SQL UPDATE "QUESTIONS"
		SET
			"CID"      = :rec.CID,
			"QUESTION" = :rec.QUESTION,
			"CHOICE1"  = :rec.CHOICE1,
			"CHOICE2"  = :rec.CHOICE2,
			"CHOICE3"  = :rec.CHOICE3,
			"CHOICE4"  = :rec.CHOICE4,
			"ANSWER"   = :rec.ANSWER
		WHERE "ID" = :rec.ID;
	EXEC SQL COMMIT;
}

// Executes a query to modify an existing category by id from the current database connection
void modify_category(void) {
	EXEC SQL BEGIN DECLARE SECTION;
		tCATEGORY rec = { 0 };
	EXEC SQL END DECLARE SECTION;

	while(cout << "Which category ID? " && !(cin >> rec.ID)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	// TODO: First initialize the record with the original dataset
	// TODO: Next check every input

	EXEC SQL UPDATE "CATEGORIES"
		SET
			"NAME" = :rec.NAME
		WHERE "ID" = :rec.ID;
	EXEC SQL COMMIT;
}

// Executes a query to insert a new question to the current database connection
void create_question(void) {
	string question, choices[4];
	int answer, category;

	// TODO: validate correct category ID
	while(cout << "Category: " && !(cin >> category)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	cout << "Question: "; getline(cin, question);
	cout << "Answer 1: "; getline(cin, choices[0]);
	cout << "Answer 2: "; getline(cin, choices[1]);
	cout << "Answer 3: "; getline(cin, choices[2]);
	cout << "Answer 4: "; getline(cin, choices[3]);

	// TODO: validate answer to be in range of 1 to 4
	while (cout << "Which is answer ist the correct one [1-4]? " && !(cin >> answer)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	cin.ignore(numeric_limits<streamsize>::max(),'\n');

	// TODO: Use record struct instead of each variable
	EXEC SQL BEGIN DECLARE SECTION;
		const int pCategoryID = category;
		const char* pQuestion = question.c_str();
		const char* pChoice1 = choices[0].c_str();
		const char* pChoice2 = choices[1].c_str();
		const char* pChoice3 = choices[2].c_str();
		const char* pChoice4 = choices[3].c_str();
		const int pAnswer = answer;
	EXEC SQL END DECLARE SECTION;

	EXEC SQL INSERT INTO "QUESTIONS"
		(
			"CID",
			"QUESTION",
			"CHOICE1",
			"CHOICE2",
			"CHOICE3",
			"CHOICE4",
			"ANSWER"
		)
		VALUES
		(
			:pCategoryID,
			:pQuestion,
			:pChoice1,
			:pChoice2,
			:pChoice3,
			:pChoice4,
			:pAnswer
		);

	EXEC SQL COMMIT;
}

// Executes a query to insert a new category to the current database connection
void create_category(void) {
	string name;
	cout << "Category Name: "; getline(cin, name);

	// TODO: Use record struct instead of variable
	EXEC SQL BEGIN DECLARE SECTION;
		const char* pName = name.c_str();
	EXEC SQL END DECLARE SECTION;

	EXEC SQL INSERT INTO "CATEGORIES" ("NAME") VALUES(:pName);
	EXEC SQL COMMIT;
}

// Prints the main help text to stdout
void print_main_help(void) {
	print_help("Help Main Menu", {
	 	{{ "new   ", "Creates a new Question/Category" }},
	 	{{ "delete", "Deletes a Question/Category" }},
	 	{{ "modify", "Modifies a Question/Category" }},
	 	{{ "list  ", "List Questions/Categories" }},
	 	{{ "exit  ", "Exit this program" }},
	 	{{ "help  ", "Print this help text again" }}
	});
}

// Prints the common help text to stdout
void print_common_help(void) {
	print_help("Help Common Menu", {
		{{ "category", "TODO" }},
		{{ "question", "TODO" }},
	 	{{ "exit    ", "Exit this context" }},
	 	{{ "help    ", "Print this help text again" }}
	});
}

// Prints the list help text to stdout
void print_list_help(void) {
	print_help("Help List Menu", {
		{{ "category", "TODO" }},
		{{ "question", "TODO" }},
		{{ "next    ", "TODO" }},
		{{ "prev    ", "TODO" }},
		{{ "exit    ", "Exit this context" }},
		{{ "help    ", "Print this help text again" }}
	});
}

// Helper function used by print_[context]_help function
void print_help(const string &title, const vector<map<string, string>> &commands) {
	cout << BLUE <<      "═════════════════════════════════════════════\n" << RESET;
	cout << BOLDWHITE << " < " << title << " >" << BLUE << "\n";
	cout <<              "═════════════════════════════════════════════\n" << RESET;
	for (const auto &cmd : commands) {
		cout << BOLDBLUE <<  " " << cmd.begin()->first << " " << RESET << ": " << cmd.begin()->second << "\n"; 
	}
	cout << BLUE <<      "═════════════════════════════════════════════\n" << RESET << flush;
}

// Prints the context switch success message to stdout
void print_status_switch(const string &context) {
	cout << GREEN << "Switchted to " << BOLDGREEN << context << RESET << GREEN << " context\n" << RESET << flush;
}

// Prints the context exit success message to stdout
void print_exit_msg(const string &context) {
	cout << RED << "Successfully exit " << BOLDRED << context << RESET << RED << " context\n" << RESET << flush;
}
