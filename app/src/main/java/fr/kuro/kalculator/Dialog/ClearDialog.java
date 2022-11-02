package fr.kuro.kalculator.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import fr.kuro.kalculator.R;

public class ClearDialog extends BottomSheetDialogFragment {

    public BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clear_history, container, false);

        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        Button clearBtn = view.findViewById(R.id.clear_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClearButtonClicked();
                dismiss();
            }
        });
        return view;
    }

    public interface BottomSheetListener {
        void onClearButtonClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getClass() + "Must implement BottomSheetListener");
        }
    }
}
