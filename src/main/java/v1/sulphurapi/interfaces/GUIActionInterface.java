package v1.sulphurapi.interfaces;

import v1.sulphurapi.core.SAPI;

import javax.swing.*;

public interface GUIActionInterface {

    /**Runs a given action asynchronously and logs a message when done.
     * This method is intended to be used for actions that may take time to complete,
     * allowing the GUI to remain responsive.
     * Use Lambda expressions to pass the action and log message:
     *<pre>
     *     runAsyncAction(() -> {
     *     // Your action code here
     *     }, "Action completed successfully.");
     *</pre>
     *
     *
     * @param action The action to run in the background.
     * @param logMessageDone The message to log when the action is completed.
     *
     * @see javax.swing.SwingUtilities
     */
    default void runAsyncAction(Runnable action, String logMessageDone) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                action.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (logMessageDone != null && !logMessageDone.isEmpty()) {
                        SAPI.getAPI().logging().raiseDebugEvent(logMessageDone);
                    }
                } catch (Exception e) {
                    SAPI.getAPI().logging().raiseErrorEvent("Error executing background task: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    default void runAsyncLightAction(Runnable action) {
        SwingUtilities.invokeLater(action);
    }
}
