package com.luqi.choresprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by luqi on 9/1/17.
 */

public class FragmentQRCode extends Fragment {

    public static String TAG = "FragmentQRCode";
    TextView textViewCode;
    ImageView imageView;
    Context mContext;
    Utils mUtils;
    Bitmap bitmapQRCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mUtils = new Utils(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qrcode, container, false);
        textViewCode = (TextView) root.findViewById(R.id.textViewQR);
        imageView = (ImageView) root.findViewById(R.id.imageViewQRCode);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bitmapQRCode = new ImageSaver(mContext).
                setFileName("qrImage.png").
                setDirectoryName("images").
                load();
        imageView.setImageBitmap(bitmapQRCode);
    }
}
