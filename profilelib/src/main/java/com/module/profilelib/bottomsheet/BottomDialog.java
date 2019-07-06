package com.module.profilelib.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.module.profilelib.R;

/**
 * Created by Abhishek on 13/06/19.
 */
public class BottomDialog
{
    protected final Builder mBuilder;
    protected TextView tvTitle,tvGallery,tvCamera;
    protected LinearLayout llGallery,llCamera,llCancel;
    protected LinearLayout vMainLayout;

    public final Builder getBuilder()
    {
        return mBuilder;
    }

    /**
     * Function of Initialize BottomSheet
     * @param builder
     * @see BottomDialog
     */
    protected BottomDialog(Builder builder)
    {
        mBuilder = builder;
        mBuilder.bottomDialog = initBottomDialog(builder);
    }

    @UiThread
    public void show()
    {
        if (mBuilder != null && mBuilder.bottomDialog != null)
            mBuilder.bottomDialog.show();
    }

    @UiThread
    public void dismiss()
    {
        if (mBuilder != null && mBuilder.bottomDialog != null)
            mBuilder.bottomDialog.dismiss();
    }

    @UiThread
    private Dialog initBottomDialog(final Builder builder)
    {
        final Dialog bottomDialog = new Dialog(builder.context, R.style.BottomDialogs);
        View view = LayoutInflater.from(builder.context).inflate(R.layout.library_bottom_dialog, null);

        vMainLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        llGallery = (LinearLayout) view.findViewById(R.id.llGallery);
        llCamera = (LinearLayout) view.findViewById(R.id.llCamera);
        llCancel = (LinearLayout) view.findViewById(R.id.llCancel);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvGallery = (TextView) view.findViewById(R.id.tvGallery);
        tvCamera = (TextView) view.findViewById(R.id.tvCamera);

        //Here set background layout color
        if(builder.backgroundColor != 0)
        {
            vMainLayout.setBackgroundColor(builder.backgroundColor);
        }
        else
        {
            vMainLayout.setBackgroundColor(Color.parseColor("#000000"));
        }

        //Here set default textview color
        if(builder.titleColor != 0)
        {
            tvTitle.setTextColor(builder.titleColor);
            tvGallery.setTextColor(builder.titleColor);
            tvCamera.setTextColor(builder.titleColor);
        }
        else
        {
            tvTitle.setTextColor(Color.parseColor("#ffffff"));
            tvGallery.setTextColor(Color.parseColor("#ffffff"));
            tvCamera.setTextColor(Color.parseColor("#ffffff"));
        }

        //Here set title of bottom dialog
        if (builder.title != null)
        {
            tvTitle.setText(builder.title);
        }
        else
        {
            tvTitle.setVisibility(View.GONE);
        }

        if (builder.btn_gallery_callback != null)
        {
            llGallery.setVisibility(View.VISIBLE);
            llGallery.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (builder.btn_gallery_callback != null)
                    {
                        builder.btn_gallery_callback.onClick(BottomDialog.this);
                    }
                    if (builder.isAutoDismiss)
                    {
                        bottomDialog.dismiss();
                    }
                }
            });
        }

        if (builder.btn_camera_callback != null)
        {
            llCamera.setVisibility(View.VISIBLE);
            llCamera.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (builder.btn_camera_callback != null)
                    {
                        builder.btn_camera_callback.onClick(BottomDialog.this);
                    }
                    if (builder.isAutoDismiss)
                    {
                        bottomDialog.dismiss();
                    }
                }
            });
        }

        if (builder.btn_remove_callback != null)
        {
            llCancel.setVisibility(View.VISIBLE);
            llCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (builder.btn_remove_callback != null)
                    {
                        builder.btn_remove_callback.onClick(BottomDialog.this);
                    }
                    if (builder.isAutoDismiss)
                    {
                        bottomDialog.dismiss();
                    }
                }
            });
        }

        bottomDialog.setContentView(view);
        bottomDialog.setCancelable(builder.isCancelable);
        bottomDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);

        return bottomDialog;
    }

    public static class Builder
    {
        protected Context context;

        // Bottom Dialog
        protected Dialog bottomDialog;

        //Title
        protected CharSequence title;

        //Title Color
        protected int titleColor = 0;

        //Background Layout Color
        protected int backgroundColor = 0;

        // Buttons
        protected ButtonCallback btn_gallery_callback, btn_camera_callback, btn_remove_callback;

        // Buttons
        protected boolean isAutoDismiss;

        // Other options
        protected boolean isCancelable;

        public Builder(@NonNull Context context)
        {
            this.context = context;
            this.isCancelable = true;
            this.isAutoDismiss = true;
        }

        /**
         * Function for Setting Title with Color, Provide Color not Resource Id.
         * @param title
         * @see BottomDialog
         * @return
         */
        public Builder setTitle(@NonNull CharSequence title)
        {
            this.title = title;
            return this;
        }

        /**
         * Function for Setting Title with Color, Provide Color not Resource Id.
         * @param titleColor
         * @see BottomDialog
         * @return
         */
        public Builder setTextColor(int titleColor)
        {
            this.titleColor = titleColor;
            return this;
        }

        /**
         * Layout Background color, Provide Color not Resource Id
         * @param backgroundColor
         * @see BottomDialog
         * @return
         */
        public Builder setBackgroundColor(int backgroundColor)
        {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * For cancel bottom sheet on backpress or touch outside the dialog
         * @param cancelable
         * @see BottomDialog
         * @return
         */
        public Builder setCancelable(boolean cancelable)
        {
            this.isCancelable = cancelable;
            return this;
        }

        public Builder autoDismiss(boolean autodismiss)
        {
            this.isAutoDismiss = autodismiss;
            return this;
        }

        /**
         * button call for gallery image
         * @param buttonCallback
         * @see BottomDialog
         * @return
         */
        public Builder onGallery(@NonNull ButtonCallback buttonCallback)
        {
            this.btn_gallery_callback = buttonCallback;
            return this;
        }

        /**
         * button call for camera
         * @param buttonCallback
         * @see BottomDialog
         * @return
         */
        public Builder onCamera(@NonNull ButtonCallback buttonCallback)
        {
            this.btn_camera_callback = buttonCallback;
            return this;
        }

        /**
         * button call for remove
         * @param buttonCallback
         * @see BottomDialog
         * @return
         */
        public Builder onRemove(@NonNull ButtonCallback buttonCallback)
        {
            this.btn_remove_callback = buttonCallback;
            return this;
        }

        @UiThread
        public BottomDialog build()
        {
            return new BottomDialog(this);
        }

        @UiThread
        public BottomDialog show()
        {
            BottomDialog bottomDialog = build();
            bottomDialog.show();
            return bottomDialog;
        }
    }

    public interface ButtonCallback
    {
        void onClick(@NonNull BottomDialog dialog);
    }
}
