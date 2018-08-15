package com.redbluekey.sciodev.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.redbluekey.sciodev.R;

public class ChooseDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ChooseDialogCallback mCallback;

    private LinearLayout m_llTakePhoto;
    private LinearLayout m_llChooseAlbum;

    public ChooseDialog(@NonNull Context context, View contentView, ChooseDialogCallback callback) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.mContext = context;
        this.mCallback = callback;

        m_llTakePhoto = contentView.findViewById(R.id.ll_take_photo);
        m_llChooseAlbum = contentView.findViewById(R.id.ll_choose_album);

        m_llTakePhoto.setOnClickListener(this);
        m_llChooseAlbum.setOnClickListener(this);


        setContentView(contentView);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_take_photo:
                if (mCallback != null)
                    mCallback.onChoose(1);
                dismiss();
                break;

            case R.id.ll_choose_album:
                if (mCallback != null)
                    mCallback.onChoose(2);
                dismiss();
                break;
        }
    }

    public interface ChooseDialogCallback {

        void onChoose(int type);
    }
}
