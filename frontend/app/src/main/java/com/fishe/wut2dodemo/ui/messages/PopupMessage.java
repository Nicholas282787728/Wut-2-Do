package com.fishe.wut2dodemo.ui.messages;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Creates the messages to be shown to the user.
 */
public class PopupMessage {
    private Snackbar snackbar;
    private static PopupMessage ourInstance = new PopupMessage();

    public static PopupMessage getInstance() {
        return ourInstance;
    }

    private PopupMessage() {
    }

    public void prepareMessage(View view, String message, int numLines) {
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
    }

    public void setMaxLines(Snackbar snackbar, int numLines) {
        assert snackbar != null;

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(numLines);
    }

    public void showMessage(Snackbar snackbar) {
        snackbar.show();
    }



    /**
     * Displays a normal message to the user.
     * @param view      The view to show the snackbar message on.
     * @param message   The message to be shown to user.
     * @param numLines  The number of lines to show to prevent truncation.
     */
    public void showNormalMessage(View view, String message, int numLines) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        setSnackbarMaxLines(snackbar, numLines);
        snackbar.show();
    }

    /**
     * When user chooses not to grant permission request, shows the user
     * a message informing that he / she is unable to enjoy full functionalities of the application
     * and prompts the user to grant permission request.
     * Displays a normal message to the user.
     * @param view              The view to show the snackbar message on.
     * @param permission        The permission to request from the user.
     * @param clickableMessage  The clickable message to be shown to user.
     * @param message           The message to be shown to user.
     * @param numLines          The number of lines to show to prevent truncation.
     * @param requestCode       Identifier for the request that was made.
     */
    public void showMessageWithPermissionRequest(View view, String permission, String clickableMessage,
                                                 String message, Activity activity, int numLines, final int requestCode) {
        Snackbar snackbar =  Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        setSnackbarAction(snackbar, permission, clickableMessage, activity, requestCode);
        setSnackbarMaxLines(snackbar, numLines);
        snackbar.show();
    }

    /**
     * Sets a clickable action to the snackbar, allowing the user to grant permission on click.
     * @param snackbar          The snackbar to add the clickable action to.
     * @param permission        The permission to request from the user.
     * @param clickableMessage  The clickable message to be shown to user.
     * @param activity          The activity in which the user will be prompted.
     * @param requestCode       Identifier for the request that was made.
     */
    private void setSnackbarAction(Snackbar snackbar, final String permission, String clickableMessage,
                                   final Activity activity, final int requestCode) {
        assert snackbar != null;

        snackbar.setAction(clickableMessage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        });
    }

    /**
     * Sets the maximum number of lines shown in snackbar to prevent truncation.
     * @param snackbar  The snackbar to set the number of lines to.
     * @param numLines  The number of lines to set to the snackbar.
     */
    private void setSnackbarMaxLines(Snackbar snackbar, int numLines) {
        assert snackbar != null;

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(numLines);
    }
}
