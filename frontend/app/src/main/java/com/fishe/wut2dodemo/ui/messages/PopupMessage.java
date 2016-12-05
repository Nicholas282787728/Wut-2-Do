package com.fishe.wut2dodemo.ui.messages;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

//@@author A0139655U
/**
 * Creates and customizes the messages to be shown to the user. Works like a builder.
 */
public class PopupMessage {
    private Snackbar snackbar;

    /**
     * Initialises snackbar with the message to be shown to the user.
     * @param view      The view to show the snackbar message on.
     * @param message   The message to be shown to user.
     */
    public PopupMessage(View view, String message) {
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
    }

    /**
     * Sets the maximum number of lines shown in snackbar to prevent truncation.
     * @param numLines  The number of lines to set to the snackbar.
     */
    public PopupMessage setMaxLines(int numLines) {
        assert snackbar != null;

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(numLines);

        return this;
    }
    /**
     * Sets a clickable action to the snackbar, allowing the user to grant permission on click.
     * @param activity          The activity in which the user will see the prompt.
     * @param permission        The permission to request from the user.
     * @param clickableMessage  The clickable message to be shown to user.
     * @param requestCode       Identifier for the request that was made.
     */
    public PopupMessage setSnackbarAction(final Activity activity, final String permission,
                                   String clickableMessage, final int requestCode) {
        assert snackbar != null;

        snackbar.setAction(clickableMessage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        });

        return this;
    }

    public void showMessage() {
        snackbar.show();
    }
}
