package format;

import java.nio.charset.Charset;

class StringPadUtil {

    private static String leftPad(String str, int size, char c) {
        if (str == null) {
            return null;
        }

        int strLength = strLength(str);
        if (size <= 0 || size <= strLength) {
            return str;
        }
        return repeat(size - strLength, c).concat(str);
    }

    private static String rightPad(String str, int size, char c) {
        if (str == null) {
            return null;
        }

        int strLength = strLength(str);
        if (size <= 0 || size <= strLength) {
            return str;
        }
        return str.concat(repeat(size - strLength, c));
    }

    private static String center(String str, int size, char c) {
        if (str == null) {
            return null;
        }

        int strLength = strLength(str);
        if (size <= 0 || size <= strLength) {
            return str;
        }
        str = leftPad(str, strLength + (size - strLength) / 2, c);
        str = rightPad(str, size, c);
        return str;
    }

    static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    static String center(String str, int size) {
        return center(str, size, ' ');
    }

    private static String repeat(int size, char c) {
        StringBuilder s = new StringBuilder();
        for (int index = 0; index < size; index++) {
            s.append(c);
        }
        return s.toString();
    }

    static int strLength(String str) {
        return strLength(str, "UTF-8");
    }

    private static int strLength(String str, String charset) {
        byte[] bytes = str.getBytes(Charset.forName(charset));
        if (bytes.length <= 0)
            return 0;
        int len = 0;
        int j = 0;
        do {
            short tmp = (short) (bytes[j] & 0xF0);
            if (tmp >= 0xB0) {
                if (tmp < 0xC0) {
                    j += 2;
                    len += 2;
                } else if ((tmp == 0xC0) || (tmp == 0xD0)) {
                    j += 2;
                    len += 2;
                } else if (tmp == 0xE0) {
                    j += 3;
                    len += 2;
                } else if (tmp == 0xF0) {
                    short tmpst0 = (short) (((short) bytes[j]) & 0x0F);
                    if (tmpst0 == 0) {
                        j += 4;
                        len += 2;
                    } else if ((tmpst0 > 0) && (tmpst0 < 12)) {
                        j += 5;
                        len += 2;
                    } else if (tmpst0 > 11) {
                        j += 6;
                        len += 2;
                    }
                }
            } else {
                j += 1;
                len += 1;
            }
        } while (j <= bytes.length - 1);
        return len;
    }
}