#include "Utils.hpp"

string utf8_substr(const string& str, const unsigned int start, const unsigned int leng) {
    if (leng == 0 || str.size() == 0) { return ""; }
    unsigned int c, i, ix, q, min = (unsigned int)string::npos, max = (unsigned int)string::npos;
    for (q = 0, i = 0, ix = str.length(); i < ix; i++, q++) {
        if (q == start){ min = i; }
        if (q <= start + leng || leng == string::npos){ max = i; }
 
        c = (unsigned char)str[i];
        if      (c >= 0 && c <= 127) i += 0;
        else if ((c & 0xE0) == 0xC0) i += 1;
        else if ((c & 0xF0) == 0xE0) i += 2;
        else if ((c & 0xF8) == 0xF0) i += 3;
        else return "";
    }

    if (q <= start + leng || leng == string::npos){ max = i; }
    if (min == string::npos || max == string::npos) { return ""; }
    return str.substr(min, max);
}
