package v1.sulphurapi.listeners.endpoints;

import v1.sulphurapi.handlers.endpoints.EndpointsHandler;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EndpointListMouseListener implements MouseListener {

    private final EndpointsHandler endpointsHandler;

    public EndpointListMouseListener(EndpointsHandler endpointsHandler) {
        this.endpointsHandler = endpointsHandler;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        checkPopup(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        checkPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        checkPopup(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * This method checks if the mouse event is a right-click and shows the popup menu if it is.
     * It also sets the selected endpoint in the grid to the one that was clicked.
     * @param e the MouseEvent that triggered the check
     */
    private void checkPopup(MouseEvent e) {
        e.getComponent().requestFocusInWindow();
        if (SwingUtilities.isRightMouseButton(e)) {
            SwingUtilities.invokeLater(() -> {
                endpointsHandler.getGrid().getEndpointList().setSelectedIndex(
                        endpointsHandler.getGrid().getEndpointList().locationToIndex(e.getPoint()));
                if (e.isPopupTrigger()) {
                    JPopupMenu menu = endpointsHandler.getMenu();
                    menu.removeAll();
                    endpointsHandler.setCurrentLoadedPath(endpointsHandler.getGrid().getEndpointList().getSelectedValue());
                    JMenu sendRequestSubmenu = endpointsHandler.createSubMenu();
                    menu.add(sendRequestSubmenu);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            });
        }
    }
}
