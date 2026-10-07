package com.bille.app.common;

import android.text.InputFilter;
import android.text.Spanned;

public final class DigitsInputFilter implements InputFilter {

    private final int maxLength;

    public DigitsInputFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder accepted = new StringBuilder();
        for (int index = start; index < end; index++) {
            char character = source.charAt(index);
            if (character >= '0' && character <= '9') {
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
}
