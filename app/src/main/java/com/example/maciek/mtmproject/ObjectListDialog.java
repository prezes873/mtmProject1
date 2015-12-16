package com.example.maciek.mtmproject;

/**
 * Created by M_Stodulski on 2015-12-16.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.lang.*;
import java.lang.Object;


public class ObjectListDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                        // Set Dialog Title
                .setTitle("Point List")
                        // Set Dialog Message
                .setMultiChoiceItems(DataBase.getInstance(getContext()).getNameList(), null, new DialogInterface.OnMultiChoiceClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // The button was clicked, so update the fact label with a new fact.

                    }
                })

                        // Positive button
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                })

                        // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                }).create();
    }
}
