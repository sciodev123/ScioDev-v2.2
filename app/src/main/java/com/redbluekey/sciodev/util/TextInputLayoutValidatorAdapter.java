package com.redbluekey.sciodev.util;

import android.support.design.widget.TextInputLayout;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

public class TextInputLayoutValidatorAdapter implements ViewDataAdapter<TextInputLayout, String> {
    @Override
    public String getData(TextInputLayout view) throws ConversionException {
        return view.getEditText().getText().toString();
    }
}
