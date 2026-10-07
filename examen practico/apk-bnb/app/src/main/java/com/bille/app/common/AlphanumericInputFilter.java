package com.bille.app.common;

import android.text.InputFilter;
import android.text.Spanned;

public final class AlphanumericInputFilter implements InputFilter {

    private final int maxLength;

    public AlphanumericInputFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder accepted = new StringBuilder();
        for (int index = start; index < end; index++) {
            char character = source.charAt(index);
            if (isLetterOrDigit(character)) {
                accepted.append(character);
            }
        }
        int room = maxLength - (dest.length() - (dend - dstart));
        if (room <= 0) {
            return "";
        }
        if (accepted.length() > room) {
            accepted.setLength(room);
        }
        if (accepted.length() == end - start && source.subSequence(start, end).toString().contentEquals(accepted)) {
            return null;
        }
        return accepted;
    }

    private static boolean isLetterOrDigit(char character) {
        return (character >= '0' && character <= '9')
                || (character >= 'A' && character <= 'Z')
                || (character >= 'a' && character <= 'z');
    }
}
