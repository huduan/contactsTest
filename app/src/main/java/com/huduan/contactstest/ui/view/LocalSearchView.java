package com.huduan.contactstest.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huduan.contactstest.R;

/**
 * Created by huduan on 17-12-3.
 */

public class LocalSearchView extends LinearLayout {


    private EditText et_search;

    private ImageView mQuit;

    private ITextChanged textChangedCallback;


    public LocalSearchView(Context context) {
        super(context);
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textChangedCallback != null) {
                    textChangedCallback.onTextChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setTextChangedCallback(ITextChanged callback) {
        textChangedCallback = callback;
    }

    public interface ITextChanged {
        void onTextChanged();
    }
}
