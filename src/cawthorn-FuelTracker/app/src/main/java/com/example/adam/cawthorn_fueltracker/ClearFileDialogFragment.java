package com.example.adam.cawthorn_fueltracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Adam on 2016-01-25.
 * Conformation Fragment for clearing all log entries.
 * Has interface. onClearFileDialogPositiveClick, and onClearFileDialogNegativeClick
 * @return Dialog
 */
public class ClearFileDialogFragment extends DialogFragment {

    public interface ClearFileDialogListener {
        void onClearFileDialogPositiveClick(DialogFragment dialogFragment);
        void onClearFileDialogNegativeClick(DialogFragment dialogFragment);
    }

    ClearFileDialogListener clearFileDialogListener;

    //adapted from http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            clearFileDialogListener = (ClearFileDialogListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() + " must implement ClearFileDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.clear_entries_confirmation)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearFileDialogListener.onClearFileDialogPositiveClick(ClearFileDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearFileDialogListener.onClearFileDialogNegativeClick(ClearFileDialogFragment.this);
                    }
                });
        return builder.create();
    }
}