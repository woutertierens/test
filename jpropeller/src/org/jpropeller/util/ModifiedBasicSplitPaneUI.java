package org.jpropeller.util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.UIResource;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.Views;



import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;


public class ModifiedBasicSplitPaneUI extends SplitPaneUI
{    /**
     * The divider used for non-continuous layout is added to the split pane
     * with this object.
     */
    protected static final String NON_CONTINUOUS_DIVIDER =
        "nonContinuousDivider";


    /**
     * How far (relative) the divider does move when it is moved around by
     * the cursor keys on the keyboard.
     */
    protected static int KEYBOARD_DIVIDER_MOVE_OFFSET = 3;


    /**
     * JSplitPane instance this instance is providing
     * the look and feel for.
     */
    protected JSplitPane splitPane;


    /**
     * LayoutManager that is created and placed into the split pane.
     */
    protected BasicHorizontalLayoutManager layoutManager;


    /**
     * Instance of the divider for this JSplitPane.
     */
    protected ModifiedBasicSplitPaneDivider divider;


    /**
     * Instance of the PropertyChangeListener for this JSplitPane.
     */
    protected PropertyChangeListener propertyChangeListener;


    /**
     * Instance of the FocusListener for this JSplitPane.
     */
    protected FocusListener focusListener;

    private Handler handler;


    /**
     * Keys to use for forward focus traversal when the JComponent is
     * managing focus.
     */
    private Set managingFocusForwardTraversalKeys;

    /**
     * Keys to use for backward focus traversal when the JComponent is
     * managing focus.
     */
    private Set managingFocusBackwardTraversalKeys;


    /**
     * The size of the divider while the dragging session is valid.
     */
    protected int dividerSize;


    /**
     * Instance for the shadow of the divider when non continuous layout
     * is being used.
     */
    protected Component nonContinuousLayoutDivider;


    /**
     * Set to true in startDragging if any of the children
     * (not including the nonContinuousLayoutDivider) are heavy weights.
     */
    protected boolean draggingHW;


    /**
     * Location of the divider when the dragging session began.
     */
    protected int beginDragDividerLocation;

    // Private data of the instance
    private int         orientation;
    private int         lastDragLocation;
    private boolean     continuousLayout;
    private boolean     dividerKeyboardResize;
    private boolean     dividerLocationIsSet;  // needed for tracking
                                               // the first occurrence of
                                               // setDividerLocation()
    private Color dividerDraggingColor;
    private boolean rememberPaneSizes;

    // Indicates wether the one of splitpane sides is expanded
    private boolean keepHidden = false;

    /** Indicates that we have painted once. */
    boolean             painted;
    /** If true, setDividerLocation does nothing. */
    boolean             ignoreDividerLocationChange;


    /**
     * Creates a new BasicSplitPaneUI instance
     */
    public static ComponentUI createUI(JComponent x) {
        return new ModifiedBasicSplitPaneUI();
    }

    /**
     * Installs the UI.
     */
    public void installUI(JComponent c) {
        splitPane = (JSplitPane) c;
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        keepHidden = false;
        installDefaults();
        installListeners();
        installKeyboardActions();
        setLastDragLocation(-1);
    }


    /**
     * Installs the UI defaults.
     */
    protected void installDefaults(){
        LookAndFeel.installBorder(splitPane, "SplitPane.border");
        LookAndFeel.installColors(splitPane, "SplitPane.background",
                                  "SplitPane.foreground");
        LookAndFeel.installProperty(splitPane, "opaque", Boolean.TRUE);

        if (divider == null) divider = createDefaultDivider();
        divider.setBasicSplitPaneUI(this);

        Border    b = divider.getBorder();

        if (b == null || !(b instanceof UIResource)) {
            divider.setBorder(UIManager.getBorder("SplitPaneDivider.border"));
        }

        dividerDraggingColor = new Color(0xDDDDDD);//UIManager.getColor("SplitPaneDivider.draggingColor");

        setOrientation(splitPane.getOrientation());

        // This plus 2 here is to provide backwards consistancy. Previously,
        // the old size did not include the 2 pixel border around the divider,
        // it now does.
        Integer dividerSize = (Integer)UIManager.get("SplitPane.dividerSize");
        if (divider == null) dividerSize = 10;
        LookAndFeel.installProperty(splitPane, "dividerSize", dividerSize);

        divider.setDividerSize(splitPane.getDividerSize());
        dividerSize = divider.getDividerSize();
        splitPane.add(divider, JSplitPane.DIVIDER);

        setContinuousLayout(splitPane.isContinuousLayout());

        resetLayoutManager();

        /* Install the nonContinuousLayoutDivider here to avoid having to
        add/remove everything later. */
        if(nonContinuousLayoutDivider == null) {
            setNonContinuousLayoutDivider(
                                createDefaultNonContinuousLayoutDivider(),
                                true);
        } else {
            setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
        }

        // focus forward traversal key
        if (managingFocusForwardTraversalKeys==null) {
            managingFocusForwardTraversalKeys = new HashSet();
            managingFocusForwardTraversalKeys.add(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        }
        splitPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                                        managingFocusForwardTraversalKeys);
        // focus backward traversal key
        if (managingFocusBackwardTraversalKeys==null) {
            managingFocusBackwardTraversalKeys = new HashSet();
            managingFocusBackwardTraversalKeys.add(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
        }
        splitPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                                        managingFocusBackwardTraversalKeys);
    }


    /**
     * Installs the event listeners for the UI.
     */
    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) !=
            null) {
            splitPane.addPropertyChangeListener(propertyChangeListener);
        }

        if ((focusListener = createFocusListener()) != null) {
            splitPane.addFocusListener(focusListener);
        }
    }


    /**
     * Installs the keyboard actions for the UI.
     */
    protected void installKeyboardActions() {
        InputMap km = getInputMap(JComponent.
                                  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        SwingUtilities.replaceUIInputMap(splitPane, JComponent.
                                       WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                       km);
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap)DefaultLookup.get(splitPane, this,
                                               "SplitPane.ancestorInputMap");
        }
        return null;
    }

    /**
     * Uninstalls the UI.
     */
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDefaults();
        dividerLocationIsSet = false;
        dividerKeyboardResize = false;
        splitPane = null;
    }


    /**
     * Uninstalls the UI defaults.
     */
    protected void uninstallDefaults() {
        if(splitPane.getLayout() == layoutManager) {
            splitPane.setLayout(null);
        }

        if(nonContinuousLayoutDivider != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }

        LookAndFeel.uninstallBorder(splitPane);

        Border    b = divider.getBorder();

        if (b instanceof UIResource) {
            divider.setBorder(null);
        }

        splitPane.remove(divider);
        divider.setBasicSplitPaneUI(null);
        layoutManager = null;
        divider = null;
        nonContinuousLayoutDivider = null;

        setNonContinuousLayoutDivider(null);

        // sets the focus forward and backward traversal keys to null
        // to restore the defaults
        splitPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        splitPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
    }


    /**
     * Uninstalls the event listeners for the UI.
     */
    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            splitPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
        if (focusListener != null) {
            splitPane.removeFocusListener(focusListener);
            focusListener = null;
        }
        handler = null;
    }


    /**
     * Uninstalls the keyboard actions for the UI.
     */
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(splitPane, null);
        SwingUtilities.replaceUIInputMap(splitPane, JComponent.
                                      WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                      null);
    }


    /**
     * Creates a PropertyChangeListener for the JSplitPane UI.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }


    /**
     * Creates a FocusListener for the JSplitPane UI.
     */
    protected FocusListener createFocusListener() {
        return getHandler();
    }


    /**
     * Returns the orientation for the JSplitPane.
     */
    public int getOrientation() {
        return orientation;
    }


    /**
     * Set the orientation for the JSplitPane.
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }


    /**
     * Determines wether the JSplitPane is set to use a continuous layout.
     */
    public boolean isContinuousLayout() {
        return continuousLayout;
    }


    /**
     * Turn continuous layout on/off.
     */
    public void setContinuousLayout(boolean b) {
        continuousLayout = b;
    }


    /**
     * Returns the last drag location of the JSplitPane.
     */
    public int getLastDragLocation() {
        return lastDragLocation;
    }


    /**
     * Set the last drag location of the JSplitPane.
     */
    public void setLastDragLocation(int l) {
        lastDragLocation = l;
    }

    /**
     * @return increment via keyboard methods.
     */
    int getKeyboardMoveIncrement() {
        return 3;
    }

    /**
     * Implementation of the PropertyChangeListener
     * that the JSplitPane UI uses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class PropertyHandler implements PropertyChangeListener
    {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.

        /**
         * Messaged from the <code>JSplitPane</code> the receiver is
         * contained in.  May potentially reset the layout manager and cause a
         * <code>validate</code> to be sent.
         */
        public void propertyChange(PropertyChangeEvent e) {
            getHandler().propertyChange(e);
        }
    }


    /**
     * Implementation of the FocusListener that the JSplitPane UI uses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class FocusHandler extends FocusAdapter
    {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void focusGained(FocusEvent ev) {
            getHandler().focusGained(ev);
        }

        public void focusLost(FocusEvent ev) {
            getHandler().focusLost(ev);
        }
    }


    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardUpLeftHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                splitPane.setDividerLocation(Math.max(0,getDividerLocation
                                  (splitPane) - getKeyboardMoveIncrement()));
            }
        }
    }

    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardDownRightHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                splitPane.setDividerLocation(getDividerLocation(splitPane) +
                                             getKeyboardMoveIncrement());
            }
        }
    }


    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardHomeHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                splitPane.setDividerLocation(0);
            }
        }
    }


    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardEndHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (dividerKeyboardResize) {
                Insets   insets = splitPane.getInsets();
                int      bottomI = (insets != null) ? insets.bottom : 0;
                int      rightI = (insets != null) ? insets.right : 0;

                if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    splitPane.setDividerLocation(splitPane.getHeight() -
                                       bottomI);
                }
                else {
                    splitPane.setDividerLocation(splitPane.getWidth() -
                                                 rightI);
                }
            }
        }
    }


    /**
     * Implementation of an ActionListener that the JSplitPane UI uses for
     * handling specific key presses.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicSplitPaneUI.
     */
    public class KeyboardResizeToggleHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ev) {
            if (!dividerKeyboardResize) {
                splitPane.requestFocus();
            }
        }
    }

    /**
     * Returns the divider between the top Components.
     */
    public ModifiedBasicSplitPaneDivider getDivider() {
        return divider;
    }


    /**
     * Returns the default non continuous layout divider, which is an
     * instanceof Canvas that fills the background in dark gray.
     */
    protected Component createDefaultNonContinuousLayoutDivider() {
        return new Canvas() {
            public void paint(Graphics g) {
                if(!isContinuousLayout() && getLastDragLocation() != -1) {
                    Dimension      size = splitPane.getSize();

                    g.setColor(dividerDraggingColor);
                    if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                        g.fillRect(0, 0, dividerSize - 1, size.height - 1);
                    } else {
                        g.fillRect(0, 0, size.width - 1, dividerSize - 1);
                    }
                }
            }
        };
    }


    /**
     * Sets the divider to use when the splitPane is configured to
     * not continuously layout. This divider will only be used during a
     * dragging session. It is recommended that the passed in component
     * be a heavy weight.
     */
    protected void setNonContinuousLayoutDivider(Component newDivider) {
        setNonContinuousLayoutDivider(newDivider, true);
    }


    /**
     * Sets the divider to use.
     */
    protected void setNonContinuousLayoutDivider(Component newDivider,
        boolean rememberSizes) {
        rememberPaneSizes = rememberSizes;
        if(nonContinuousLayoutDivider != null && splitPane != null) {
            splitPane.remove(nonContinuousLayoutDivider);
        }
        nonContinuousLayoutDivider = newDivider;
    }

    private void addHeavyweightDivider() {
        if(nonContinuousLayoutDivider != null && splitPane != null) {

            /* Needs to remove all the components and re-add them! YECK! */
            // This is all done so that the nonContinuousLayoutDivider will
            // be drawn on top of the other components, without this, one
            // of the heavyweights will draw over the divider!
            Component             leftC = splitPane.getLeftComponent();
            Component             rightC = splitPane.getRightComponent();
            int                   lastLocation = splitPane.
                                              getDividerLocation();

            if(leftC != null)
                splitPane.setLeftComponent(null);
            if(rightC != null)
                splitPane.setRightComponent(null);
            splitPane.remove(divider);
            splitPane.add(nonContinuousLayoutDivider, ModifiedBasicSplitPaneUI.
                          NON_CONTINUOUS_DIVIDER,
                          splitPane.getComponentCount());
            splitPane.setLeftComponent(leftC);
            splitPane.setRightComponent(rightC);
            splitPane.add(divider, JSplitPane.DIVIDER);
            if(rememberPaneSizes) {
                splitPane.setDividerLocation(lastLocation);
            }
        }

    }


    /**
     * Returns the divider to use when the splitPane is configured to
     * not continuously layout. This divider will only be used during a
     * dragging session.
     */
    public Component getNonContinuousLayoutDivider() {
        return nonContinuousLayoutDivider;
    }


    /**
     * Returns the splitpane this instance is currently contained
     * in.
     */
    public JSplitPane getSplitPane() {
        return splitPane;
    }


    /**
     * Creates the default divider.
     */
    public ModifiedBasicSplitPaneDivider createDefaultDivider() {
        return new ModifiedBasicSplitPaneDivider(this);
    }


    /**
     * Messaged to reset the preferred sizes.
     */
    public void resetToPreferredSizes(JSplitPane jc) {
        if(splitPane != null) {
            layoutManager.resetToPreferredSizes();
            splitPane.revalidate();
            splitPane.repaint();
        }
    }


    /**
     * Sets the location of the divider to location.
     */
    public void setDividerLocation(JSplitPane jc, int location) {
        if (!ignoreDividerLocationChange) {
            dividerLocationIsSet = true;
            splitPane.revalidate();
            splitPane.repaint();

            if (keepHidden) {
                Insets insets = splitPane.getInsets();
                int orientation = splitPane.getOrientation();
                if ((orientation == JSplitPane.VERTICAL_SPLIT &&
                     location != insets.top &&
                     location != splitPane.getHeight()-divider.getHeight()-insets.top) ||
                    (orientation == JSplitPane.HORIZONTAL_SPLIT &&
                     location != insets.left &&
                     location != splitPane.getWidth()-divider.getWidth()-insets.left)) {
                    setKeepHidden(false);
                }
            }
        }
        else {
            ignoreDividerLocationChange = false;
        }
    }


    /**
     * Returns the location of the divider, which may differ from what
     * the splitpane thinks the location of the divider is.
     */
    public int getDividerLocation(JSplitPane jc) {
        if(orientation == JSplitPane.HORIZONTAL_SPLIT)
            return divider.getLocation().x;
        return divider.getLocation().y;
    }


    /**
     * Gets the minimum location of the divider.
     */
    public int getMinimumDividerLocation(JSplitPane jc) {
        int       minLoc = 0;
        Component leftC = splitPane.getLeftComponent();

        if ((leftC != null) && (leftC.isVisible())) {
            Insets    insets = splitPane.getInsets();
            Dimension minSize = leftC.getMinimumSize();
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                minLoc = minSize.width;
            } else {
                minLoc = minSize.height;
            }
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    minLoc += insets.left;
                } else {
                    minLoc += insets.top;
                }
            }
        }
        return minLoc;
    }


    /**
     * Gets the maximum location of the divider.
     */
    public int getMaximumDividerLocation(JSplitPane jc) {
        Dimension splitPaneSize = splitPane.getSize();
        int       maxLoc = 0;
        Component rightC = splitPane.getRightComponent();

        if (rightC != null) {
            Insets    insets = splitPane.getInsets();
            Dimension minSize = new Dimension(0, 0);
            if (rightC.isVisible()) {
                minSize = rightC.getMinimumSize();
            }
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                maxLoc = splitPaneSize.width - minSize.width;
            } else {
                maxLoc = splitPaneSize.height - minSize.height;
            }
            maxLoc -= dividerSize;
            if(insets != null) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    maxLoc -= insets.right;
                } else {
                    maxLoc -= insets.top;
                }
            }
        }
        return Math.max(getMinimumDividerLocation(splitPane), maxLoc);
    }


    /**
     * Messaged after the JSplitPane the receiver is providing the look
     * and feel for paints its children.
     */
    public void finishedPaintingChildren(JSplitPane jc, Graphics g) {
        if(jc == splitPane && getLastDragLocation() != -1 &&
           !isContinuousLayout() && !draggingHW) {
            Dimension      size = splitPane.getSize();

            g.setColor(dividerDraggingColor);
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                g.fillRect(getLastDragLocation(), 0, dividerSize - 1,
                           size.height - 1);
            } else {
                g.fillRect(0, lastDragLocation, size.width - 1,
                           dividerSize - 1);
            }
        }
    }


    /**
     * Messaged to paint the look and feel.
     */
    public void paint(Graphics g, JComponent jc) {
        if (!painted && splitPane.getDividerLocation()<0) {
            ignoreDividerLocationChange = true;
            splitPane.setDividerLocation(getDividerLocation(splitPane));
        }
        painted = true;
    }


    /**
     * Returns the preferred size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getPreferredSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.preferredLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the minimum size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getMinimumSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.minimumLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the maximum size for the passed in component,
     * This is passed off to the current layoutmanager.
     */
    public Dimension getMaximumSize(JComponent jc) {
        if(splitPane != null)
            return layoutManager.maximumLayoutSize(splitPane);
        return new Dimension(0, 0);
    }


    /**
     * Returns the insets. The insets are returned from the border insets
     * of the current border.
     */
    public Insets getInsets(JComponent jc) {
        return null;
    }


    /**
     * Resets the layout manager based on orientation and messages it
     * with invalidateLayout to pull in appropriate Components.
     */
    protected void resetLayoutManager() {
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            layoutManager = new BasicHorizontalLayoutManager(0);
        } else {
            layoutManager = new BasicHorizontalLayoutManager(1);
        }
        splitPane.setLayout(layoutManager);
        layoutManager.updateComponents();
        splitPane.revalidate();
        splitPane.repaint();
    }

    /**
     * Set the value to indicate if one of the splitpane sides is expanded.
     */
    void setKeepHidden(boolean keepHidden) {
        this.keepHidden = keepHidden;
    }

    /**
     * The value returned indicates if one of the splitpane sides is expanded.
     * @return true if one of the splitpane sides is expanded, false otherwise.
     */
    private boolean getKeepHidden() {
        return keepHidden;
    }

    /**
     * Should be messaged before the dragging session starts, resets
     * lastDragLocation and dividerSize.
     */
    protected void startDragging() {
        Component       leftC = splitPane.getLeftComponent();
        Component       rightC = splitPane.getRightComponent();
        ComponentPeer   cPeer;

        beginDragDividerLocation = getDividerLocation(splitPane);
        draggingHW = false;
        if(leftC != null && (cPeer = leftC.getPeer()) != null &&
           !(cPeer instanceof LightweightPeer)) {
            draggingHW = true;
        } else if(rightC != null && (cPeer = rightC.getPeer()) != null
                  && !(cPeer instanceof LightweightPeer)) {
            draggingHW = true;
        }
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            setLastDragLocation(divider.getBounds().x);
            dividerSize = divider.getSize().width;
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (getLastDragLocation(), 0, dividerSize,
                         splitPane.getHeight());
                      addHeavyweightDivider();
            }
        } else {
            setLastDragLocation(divider.getBounds().y);
            dividerSize = divider.getSize().height;
            if(!isContinuousLayout() && draggingHW) {
                nonContinuousLayoutDivider.setBounds
                        (0, getLastDragLocation(), splitPane.getWidth(),
                         dividerSize);
                      addHeavyweightDivider();
            }
        }
    }


    /**
     * Messaged during a dragging session to move the divider to the
     * passed in location. If continuousLayout is true the location is
     * reset and the splitPane validated.
     */
    protected void dragDividerTo(int location) {
        if(getLastDragLocation() != location) {
            if(isContinuousLayout()) {
                splitPane.setDividerLocation(location);
                setLastDragLocation(location);
            } else {
                int lastLoc = getLastDragLocation();

                setLastDragLocation(location);
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(
                            getLastDragLocation(), 0);
                    } else {
                        int   splitHeight = splitPane.getHeight();
                        splitPane.repaint(lastLoc, 0, dividerSize,
                                          splitHeight);
                        splitPane.repaint(location, 0, dividerSize,
                                          splitHeight);
                    }
                } else {
                    if(draggingHW) {
                        nonContinuousLayoutDivider.setLocation(0,
                            getLastDragLocation());
                    } else {
                        int    splitWidth = splitPane.getWidth();

                        splitPane.repaint(0, lastLoc, splitWidth,
                                          dividerSize);
                        splitPane.repaint(0, location, splitWidth,
                                          dividerSize);
                    }
                }
            }
        }
    }


    /**
     * Messaged to finish the dragging session. If not continuous display
     * the dividers location will be reset.
     */
    protected void finishDraggingTo(int location) {
        dragDividerTo(location);
        setLastDragLocation(-1);
        if(!isContinuousLayout()) {
            Component   leftC = splitPane.getLeftComponent();
            Rectangle   leftBounds = leftC.getBounds();

            if (draggingHW) {
                if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    nonContinuousLayoutDivider.setLocation(-dividerSize, 0);
                }
                else {
                    nonContinuousLayoutDivider.setLocation(0, -dividerSize);
                }
                splitPane.remove(nonContinuousLayoutDivider);
            }
            splitPane.setDividerLocation(location);
        }
    }


    /**
     * As of Java 2 platform v1.3 this method is no longer used. Instead
     * you should set the border on the divider.
     * <p>
     * Returns the width of one side of the divider border.
     *
     * @deprecated As of Java 2 platform v1.3, instead set the border on the
     * divider.
     */
    @Deprecated
    protected int getDividerBorderSize() {
        return 1;
    }


    /**
     * LayoutManager for JSplitPanes that have an orientation of
     * HORIZONTAL_SPLIT.
     */
    public class BasicHorizontalLayoutManager implements LayoutManager2
    {
        /* left, right, divider. (in this exact order) */
        protected int[]         sizes;
        protected Component[]   components;
        /** Size of the splitpane the last time laid out. */
        private int             lastSplitPaneSize;
        /** True if resetToPreferredSizes has been invoked. */
        private boolean         doReset;
        /** Axis, 0 for horizontal, or 1 for veritcal. */
        private int             axis;


        BasicHorizontalLayoutManager() {
            this(0);
        }

        BasicHorizontalLayoutManager(int axis) {
            this.axis = axis;
            components = new Component[3];
            components[0] = components[1] = components[2] = null;
            sizes = new int[3];
        }

        //
        // LayoutManager
        //

        /**
         * Does the actual layout.
         */
        public void layoutContainer(Container container) {
            Dimension   containerSize = container.getSize();

            // If the splitpane has a zero size then no op out of here.
            // If we execute this function now, we're going to cause ourselves
            // much grief.
            if (containerSize.height <= 0 || containerSize.width <= 0 ) {
                lastSplitPaneSize = 0;
                return;
            }

            int         spDividerLocation = splitPane.getDividerLocation();
            Insets      insets = splitPane.getInsets();
            int         availableSize = getAvailableSize(containerSize,
                                                         insets);
            int         newSize = getSizeForPrimaryAxis(containerSize);
            int         beginLocation = getDividerLocation(splitPane);
            int         dOffset = getSizeForPrimaryAxis(insets, true);
            Dimension   dSize = (components[2] == null) ? null :
                                 components[2].getPreferredSize();

            if ((doReset && !dividerLocationIsSet) || spDividerLocation < 0) {
                resetToPreferredSizes(availableSize);
            }
            else if (lastSplitPaneSize <= 0 ||
                     availableSize == lastSplitPaneSize || !painted ||
                     (dSize != null &&
                      getSizeForPrimaryAxis(dSize) != sizes[2])) {
                if (dSize != null) {
                    sizes[2] = getSizeForPrimaryAxis(dSize);
                }
                else {
                    sizes[2] = 0;
                }
                setDividerLocation(spDividerLocation - dOffset, availableSize);
                dividerLocationIsSet = false;
            }
            else if (availableSize != lastSplitPaneSize) {
                distributeSpace(availableSize - lastSplitPaneSize,
                                getKeepHidden());
            }
            doReset = false;
            dividerLocationIsSet = false;
            lastSplitPaneSize = availableSize;

            // Reset the bounds of each component
            int nextLocation = getInitialLocation(insets);
            int counter = 0;

            while (counter < 3) {
                if (components[counter] != null &&
                    components[counter].isVisible()) {
                    setComponentToSize(components[counter], sizes[counter],
                                       nextLocation, insets, containerSize);
                    nextLocation += sizes[counter];
                }
                switch (counter) {
                case 0:
                    counter = 2;
                    break;
                case 2:
                    counter = 1;
                    break;
                case 1:
                    counter = 3;
                    break;
                }
            }
            if (painted) {
                // This is tricky, there is never a good time for us
                // to push the value to the splitpane, painted appears to
                // the best time to do it. What is really needed is
                // notification that layout has completed.
                int      newLocation = getDividerLocation(splitPane);

                if (newLocation != (spDividerLocation - dOffset)) {
                    int  lastLocation = splitPane.getLastDividerLocation();

                    ignoreDividerLocationChange = true;
                    try {
                        splitPane.setDividerLocation(newLocation);
                        // This is not always needed, but is rather tricky
                        // to determine when... The case this is needed for
                        // is if the user sets the divider location to some
                        // bogus value, say 0, and the actual value is 1, the
                        // call to setDividerLocation(1) will preserve the
                        // old value of 0, when we really want the divider
                        // location value  before the call. This is needed for
                        // the one touch buttons.
                        splitPane.setLastDividerLocation(lastLocation);
                    } finally {
                        ignoreDividerLocationChange = false;
                    }
                }
            }
        }


        /**
         * Adds the component at place.  Place must be one of
         * JSplitPane.LEFT, RIGHT, TOP, BOTTOM, or null (for the
         * divider).
         */
        public void addLayoutComponent(String place, Component component) {
            boolean isValid = true;

            if(place != null) {
                if(place.equals(JSplitPane.DIVIDER)) {
                    /* Divider. */
                    components[2] = component;
                    sizes[2] = getSizeForPrimaryAxis(component.
                                                     getPreferredSize());
                } else if(place.equals(JSplitPane.LEFT) ||
                          place.equals(JSplitPane.TOP)) {
                    components[0] = component;
                    sizes[0] = 0;
                } else if(place.equals(JSplitPane.RIGHT) ||
                          place.equals(JSplitPane.BOTTOM)) {
                    components[1] = component;
                    sizes[1] = 0;
                } else if(!place.equals(
                                    ModifiedBasicSplitPaneUI.NON_CONTINUOUS_DIVIDER))
                    isValid = false;
            } else {
                isValid = false;
            }
            if(!isValid)
                throw new IllegalArgumentException("cannot add to layout: " +
                    "unknown constraint: " +
                    place);
            doReset = true;
        }


        /**
         * Returns the minimum size needed to contain the children.
         * The width is the sum of all the childrens min widths and
         * the height is the largest of the childrens minimum heights.
         */
        public Dimension minimumLayoutSize(Container container) {
            int         minPrimary = 0;
            int         minSecondary = 0;
            Insets      insets = splitPane.getInsets();

            for (int counter=0; counter<3; counter++) {
                if(components[counter] != null) {
                    Dimension   minSize = components[counter].getMinimumSize();
                    int         secSize = getSizeForSecondaryAxis(minSize);

                    minPrimary += getSizeForPrimaryAxis(minSize);
                    if(secSize > minSecondary)
                        minSecondary = secSize;
                }
            }
            if(insets != null) {
                minPrimary += getSizeForPrimaryAxis(insets, true) +
                              getSizeForPrimaryAxis(insets, false);
                minSecondary += getSizeForSecondaryAxis(insets, true) +
                              getSizeForSecondaryAxis(insets, false);
            }
            if (axis == 0) {
                return new Dimension(minPrimary, minSecondary);
            }
            return new Dimension(minSecondary, minPrimary);
        }


        /**
         * Returns the preferred size needed to contain the children.
         * The width is the sum of all the childrens preferred widths and
         * the height is the largest of the childrens preferred heights.
         */
        public Dimension preferredLayoutSize(Container container) {
            int         prePrimary = 0;
            int         preSecondary = 0;
            Insets      insets = splitPane.getInsets();

            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] != null) {
                    Dimension   preSize = components[counter].
                                          getPreferredSize();
                    int         secSize = getSizeForSecondaryAxis(preSize);

                    prePrimary += getSizeForPrimaryAxis(preSize);
                    if(secSize > preSecondary)
                        preSecondary = secSize;
                }
            }
            if(insets != null) {
                prePrimary += getSizeForPrimaryAxis(insets, true) +
                              getSizeForPrimaryAxis(insets, false);
                preSecondary += getSizeForSecondaryAxis(insets, true) +
                              getSizeForSecondaryAxis(insets, false);
            }
            if (axis == 0) {
                return new Dimension(prePrimary, preSecondary);
            }
            return new Dimension(preSecondary, prePrimary);
        }


        /**
         * Removes the specified component from our knowledge.
         */
        public void removeLayoutComponent(Component component) {
            for(int counter = 0; counter < 3; counter++) {
                if(components[counter] == component) {
                    components[counter] = null;
                    sizes[counter] = 0;
                    doReset = true;
                }
            }
        }


        //
        // LayoutManager2
        //


        /**
         * Adds the specified component to the layout, using the specified
         * constraint object.
         * @param comp the component to be added
         * @param constraints  where/how the component is added to the layout.
         */
        public void addLayoutComponent(Component comp, Object constraints) {
            if ((constraints == null) || (constraints instanceof String)) {
                addLayoutComponent((String)constraints, comp);
            } else {
                throw new IllegalArgumentException("cannot add to layout: " +
                                                   "constraint must be a " +
                                                   "string (or null)");
            }
        }


        /**
         * Returns the alignment along the x axis.  This specifies how
         * the component would like to be aligned relative to other
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentX(Container target) {
            return 0.0f;
        }


        /**
         * Returns the alignment along the y axis.  This specifies how
         * the component would like to be aligned relative to other
         * components.  The value should be a number between 0 and 1
         * where 0 represents alignment along the origin, 1 is aligned
         * the furthest away from the origin, 0.5 is centered, etc.
         */
        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }


        /**
         * Does nothing. If the developer really wants to change the
         * size of one of the views JSplitPane.resetToPreferredSizes should
         * be messaged.
         */
        public void invalidateLayout(Container c) {
        }


        /**
         * Returns the maximum layout size, which is Integer.MAX_VALUE
         * in both directions.
         */
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }


        //
        // New methods.
        //

        /**
         * Marks the receiver so that the next time this instance is
         * laid out it'll ask for the preferred sizes.
         */
        public void resetToPreferredSizes() {
            doReset = true;
        }

        /**
         * Resets the size of the Component at the passed in location.
         */
        protected void resetSizeAt(int index) {
            sizes[index] = 0;
            doReset = true;
        }


        /**
         * Sets the sizes to <code>newSizes</code>.
         */
        protected void setSizes(int[] newSizes) {
            System.arraycopy(newSizes, 0, sizes, 0, 3);
        }


        /**
         * Returns the sizes of the components.
         */
        protected int[] getSizes() {
            int[]         retSizes = new int[3];

            System.arraycopy(sizes, 0, retSizes, 0, 3);
            return retSizes;
        }


        /**
         * Returns the width of the passed in Components preferred size.
         */
        protected int getPreferredSizeOfComponent(Component c) {
            return getSizeForPrimaryAxis(c.getPreferredSize());
        }


        /**
         * Returns the width of the passed in Components minimum size.
         */
        int getMinimumSizeOfComponent(Component c) {
            return getSizeForPrimaryAxis(c.getMinimumSize());
        }


        /**
         * Returns the width of the passed in component.
         */
        protected int getSizeOfComponent(Component c) {
            return getSizeForPrimaryAxis(c.getSize());
        }


        /**
         * Returns the available width based on the container size and
         * Insets.
         */
        protected int getAvailableSize(Dimension containerSize,
                                       Insets insets) {
            if(insets == null)
                return getSizeForPrimaryAxis(containerSize);
            return (getSizeForPrimaryAxis(containerSize) -
                    (getSizeForPrimaryAxis(insets, true) +
                     getSizeForPrimaryAxis(insets, false)));
        }


        /**
         * Returns the left inset, unless the Insets are null in which case
         * 0 is returned.
         */
        protected int getInitialLocation(Insets insets) {
            if(insets != null)
                return getSizeForPrimaryAxis(insets, true);
            return 0;
        }


        /**
         * Sets the width of the component c to be size, placing its
         * x location at location, y to the insets.top and height
         * to the containersize.height less the top and bottom insets.
         */
        protected void setComponentToSize(Component c, int size,
                                          int location, Insets insets,
                                          Dimension containerSize) {
            if(insets != null) {
                if (axis == 0) {
                    c.setBounds(location, insets.top, size,
                                containerSize.height -
                                (insets.top + insets.bottom));
                }
                else {
                    c.setBounds(insets.left, location, containerSize.width -
                                (insets.left + insets.right), size);
                }
            }
            else {
                if (axis == 0) {
                    c.setBounds(location, 0, size, containerSize.height);
                }
                else {
                    c.setBounds(0, location, containerSize.width, size);
                }
            }
        }

        /**
         * If the axis == 0, the width is returned, otherwise the height.
         */
        int getSizeForPrimaryAxis(Dimension size) {
            if (axis == 0) {
                return size.width;
            }
            return size.height;
        }

        /**
         * If the axis == 0, the width is returned, otherwise the height.
         */
        int getSizeForSecondaryAxis(Dimension size) {
            if (axis == 0) {
                return size.height;
            }
            return size.width;
        }

        /**
         * Returns a particular value of the inset identified by the
         * axis and <code>isTop</code><p>
         *   axis isTop
         *    0    true    - left
         *    0    false   - right
         *    1    true    - top
         *    1    false   - bottom
         */
        int getSizeForPrimaryAxis(Insets insets, boolean isTop) {
            if (axis == 0) {
                if (isTop) {
                    return insets.left;
                }
                return insets.right;
            }
            if (isTop) {
                return insets.top;
            }
            return insets.bottom;
        }

        /**
         * Returns a particular value of the inset identified by the
         * axis and <code>isTop</code><p>
         *   axis isTop
         *    0    true    - left
         *    0    false   - right
         *    1    true    - top
         *    1    false   - bottom
         */
        int getSizeForSecondaryAxis(Insets insets, boolean isTop) {
            if (axis == 0) {
                if (isTop) {
                    return insets.top;
                }
                return insets.bottom;
            }
            if (isTop) {
                return insets.left;
            }
            return insets.right;
        }

        /**
         * Determines the components. This should be called whenever
         * a new instance of this is installed into an existing
         * SplitPane.
         */
        protected void updateComponents() {
            Component comp;

            comp = splitPane.getLeftComponent();
            if(components[0] != comp) {
                components[0] = comp;
                if(comp == null) {
                    sizes[0] = 0;
                } else {
                    sizes[0] = -1;
                }
            }

            comp = splitPane.getRightComponent();
            if(components[1] != comp) {
                components[1] = comp;
                if(comp == null) {
                    sizes[1] = 0;
                } else {
                    sizes[1] = -1;
                }
            }

            /* Find the divider. */
            Component[] children = splitPane.getComponents();
            Component   oldDivider = components[2];

            components[2] = null;
            for(int counter = children.length - 1; counter >= 0; counter--) {
                if(children[counter] != components[0] &&
                   children[counter] != components[1] &&
                   children[counter] != nonContinuousLayoutDivider) {
                    if(oldDivider != children[counter]) {
                        components[2] = children[counter];
                    } else {
                        components[2] = oldDivider;
                    }
                    break;
                }
            }
            if(components[2] == null) {
                sizes[2] = 0;
            }
            else {
                sizes[2] = getSizeForPrimaryAxis(components[2].getPreferredSize());
            }
        }

        /**
         * Resets the size of the first component to <code>leftSize</code>,
         * and the right component to the remainder of the space.
         */
        void setDividerLocation(int leftSize, int availableSize) {
            boolean          lValid = (components[0] != null &&
                                       components[0].isVisible());
            boolean          rValid = (components[1] != null &&
                                       components[1].isVisible());
            boolean          dValid = (components[2] != null &&
                                       components[2].isVisible());
            int              max = availableSize;

            if (dValid) {
                max -= sizes[2];
            }
            leftSize = Math.max(0, Math.min(leftSize, max));
            if (lValid) {
                if (rValid) {
                    sizes[0] = leftSize;
                    sizes[1] = max - leftSize;
                }
                else {
                    sizes[0] = max;
                    sizes[1] = 0;
                }
            }
            else if (rValid) {
                sizes[1] = max;
                sizes[0] = 0;
            }
        }

        /**
         * Returns an array of the minimum sizes of the components.
         */
        int[] getPreferredSizes() {
            int[]         retValue = new int[3];

            for (int counter = 0; counter < 3; counter++) {
                if (components[counter] != null &&
                    components[counter].isVisible()) {
                    retValue[counter] = getPreferredSizeOfComponent
                                        (components[counter]);
                }
                else {
                    retValue[counter] = -1;
                }
            }
            return retValue;
        }

        /**
         * Returns an array of the minimum sizes of the components.
         */
        int[] getMinimumSizes() {
            int[]         retValue = new int[3];

            for (int counter = 0; counter < 2; counter++) {
                if (components[counter] != null &&
                    components[counter].isVisible()) {
                    retValue[counter] = getMinimumSizeOfComponent
                                        (components[counter]);
                }
                else {
                    retValue[counter] = -1;
                }
            }
            retValue[2] = (components[2] != null) ?
                getMinimumSizeOfComponent(components[2]) : -1;
            return retValue;
        }

        /**
         * Resets the components to their preferred sizes.
         */
        void resetToPreferredSizes(int availableSize) {
            // Set the sizes to the preferred sizes (if fits), otherwise
            // set to min sizes and distribute any extra space.
            int[]       testSizes = getPreferredSizes();
            int         totalSize = 0;

            for (int counter = 0; counter < 3; counter++) {
                if (testSizes[counter] != -1) {
                    totalSize += testSizes[counter];
                }
            }
            if (totalSize > availableSize) {
                testSizes = getMinimumSizes();

                totalSize = 0;
                for (int counter = 0; counter < 3; counter++) {
                    if (testSizes[counter] != -1) {
                        totalSize += testSizes[counter];
                    }
                }
            }
            setSizes(testSizes);
            distributeSpace(availableSize - totalSize, false);
        }

        /**
         * Distributes <code>space</code> between the two components
         * (divider won't get any extra space) based on the weighting. This
         * attempts to honor the min size of the components.
         *
         * @param keepHidden if true and one of the components is 0x0
         *                   it gets none of the extra space
         */
        void distributeSpace(int space, boolean keepHidden) {
            boolean          lValid = (components[0] != null &&
                                       components[0].isVisible());
            boolean          rValid = (components[1] != null &&
                                       components[1].isVisible());

            if (keepHidden) {
                if (lValid && getSizeForPrimaryAxis(
                                 components[0].getSize()) == 0) {
                    lValid = false;
                    if (rValid && getSizeForPrimaryAxis(
                                     components[1].getSize()) == 0) {
                        // Both aren't valid, force them both to be valid
                        lValid = true;
                    }
                }
                else if (rValid && getSizeForPrimaryAxis(
                                   components[1].getSize()) == 0) {
                    rValid = false;
                }
            }
            if (lValid && rValid) {
                double        weight = splitPane.getResizeWeight();
                int           lExtra = (int)(weight * (double)space);
                int           rExtra = (space - lExtra);

                sizes[0] += lExtra;
                sizes[1] += rExtra;

                int           lMin = getMinimumSizeOfComponent(components[0]);
                int           rMin = getMinimumSizeOfComponent(components[1]);
                boolean       lMinValid = (sizes[0] >= lMin);
                boolean       rMinValid = (sizes[1] >= rMin);

                if (!lMinValid && !rMinValid) {
                    if (sizes[0] < 0) {
                        sizes[1] += sizes[0];
                        sizes[0] = 0;
                    }
                    else if (sizes[1] < 0) {
                        sizes[0] += sizes[1];
                        sizes[1] = 0;
                    }
                }
                else if (!lMinValid) {
                    if (sizes[1] - (lMin - sizes[0]) < rMin) {
                        // both below min, just make sure > 0
                        if (sizes[0] < 0) {
                            sizes[1] += sizes[0];
                            sizes[0] = 0;
                        }
                    }
                    else {
                        sizes[1] -= (lMin - sizes[0]);
                        sizes[0] = lMin;
                    }
                }
                else if (!rMinValid) {
                    if (sizes[0] - (rMin - sizes[1]) < lMin) {
                        // both below min, just make sure > 0
                        if (sizes[1] < 0) {
                            sizes[0] += sizes[1];
                            sizes[1] = 0;
                        }
                    }
                    else {
                        sizes[0] -= (rMin - sizes[1]);
                        sizes[1] = rMin;
                    }
                }
                if (sizes[0] < 0) {
                    sizes[0] = 0;
                }
                if (sizes[1] < 0) {
                    sizes[1] = 0;
                }
            }
            else if (lValid) {
                sizes[0] = Math.max(0, sizes[0] + space);
            }
            else if (rValid) {
                sizes[1] = Math.max(0, sizes[1] + space);
            }
        }
    }


    /**
     * LayoutManager used for JSplitPanes with an orientation of
     * VERTICAL_SPLIT.
     * <p>
     */
    public class BasicVerticalLayoutManager extends
            BasicHorizontalLayoutManager
    {
        public BasicVerticalLayoutManager() {
            super(1);
        }
    }


    private class Handler implements FocusListener, PropertyChangeListener {
        //
        // PropertyChangeListener
        //
        /**
         * Messaged from the <code>JSplitPane</code> the receiver is
         * contained in.  May potentially reset the layout manager and cause a
         * <code>validate</code> to be sent.
         */
        public void propertyChange(PropertyChangeEvent e) {
            if(e.getSource() == splitPane) {
                String changeName = e.getPropertyName();

                if(changeName == JSplitPane.ORIENTATION_PROPERTY) {
                    orientation = splitPane.getOrientation();
                    resetLayoutManager();
                } else if(changeName == JSplitPane.CONTINUOUS_LAYOUT_PROPERTY){
                    setContinuousLayout(splitPane.isContinuousLayout());
                    if(!isContinuousLayout()) {
                        if(nonContinuousLayoutDivider == null) {
                            setNonContinuousLayoutDivider(
                                createDefaultNonContinuousLayoutDivider(),
                                true);
                        } else if(nonContinuousLayoutDivider.getParent() ==
                                  null) {
                            setNonContinuousLayoutDivider(
                                nonContinuousLayoutDivider,
                                true);
                        }
                    }
                } else if(changeName == JSplitPane.DIVIDER_SIZE_PROPERTY){
                    divider.setDividerSize(splitPane.getDividerSize());
                    dividerSize = divider.getDividerSize();
                    splitPane.revalidate();
                    splitPane.repaint();
                }
            }
        }

        //
        // FocusListener
        //
        public void focusGained(FocusEvent ev) {
            dividerKeyboardResize = true;
            splitPane.repaint();
        }

        public void focusLost(FocusEvent ev) {
            dividerKeyboardResize = false;
            splitPane.repaint();
        }
    }


    private static class Actions extends UIAction {
        private static final String NEGATIVE_INCREMENT = "negativeIncrement";
        private static final String POSITIVE_INCREMENT = "positiveIncrement";
        private static final String SELECT_MIN = "selectMin";
        private static final String SELECT_MAX = "selectMax";
        private static final String START_RESIZE = "startResize";
        private static final String TOGGLE_FOCUS = "toggleFocus";
        private static final String FOCUS_OUT_FORWARD = "focusOutForward";
        private static final String FOCUS_OUT_BACKWARD = "focusOutBackward";

        Actions(String key) {
            super(key);
        }

        public void actionPerformed(ActionEvent ev) {
            JSplitPane splitPane = (JSplitPane)ev.getSource();
            ModifiedBasicSplitPaneUI ui = (ModifiedBasicSplitPaneUI)splitPane.getUI();/*(BasicSplitPaneUI)BasicLookAndFeel.
                      getUIOfType(splitPane.getUI(), BasicSplitPaneUI.class);*/

            if (ui == null) {
                return;
            }
            String key = getName();
            if (key == NEGATIVE_INCREMENT) {
                if (ui.dividerKeyboardResize) {
                    splitPane.setDividerLocation(Math.max(
                              0, ui.getDividerLocation
                              (splitPane) - ui.getKeyboardMoveIncrement()));
                }
            }
            else if (key == POSITIVE_INCREMENT) {
                if (ui.dividerKeyboardResize) {
                    splitPane.setDividerLocation(
                        ui.getDividerLocation(splitPane) +
                        ui.getKeyboardMoveIncrement());
                }
            }
            else if (key == SELECT_MIN) {
                if (ui.dividerKeyboardResize) {
                    splitPane.setDividerLocation(0);
                }
            }
            else if (key == SELECT_MAX) {
                if (ui.dividerKeyboardResize) {
                    Insets   insets = splitPane.getInsets();
                    int      bottomI = (insets != null) ? insets.bottom : 0;
                    int      rightI = (insets != null) ? insets.right : 0;

                    if (ui.orientation == JSplitPane.VERTICAL_SPLIT) {
                        splitPane.setDividerLocation(splitPane.getHeight() -
                                                     bottomI);
                    }
                    else {
                        splitPane.setDividerLocation(splitPane.getWidth() -
                                                     rightI);
                    }
                }
            }
            else if (key == START_RESIZE) {
                if (!ui.dividerKeyboardResize) {
                    splitPane.requestFocus();
                } else {
                    JSplitPane parentSplitPane =
                        (JSplitPane)SwingUtilities.getAncestorOfClass(
                                         JSplitPane.class, splitPane);
                    if (parentSplitPane!=null) {
                        parentSplitPane.requestFocus();
                    }
                }
            }
            else if (key == TOGGLE_FOCUS) {
                toggleFocus(splitPane);
            }
            else if (key == FOCUS_OUT_FORWARD) {
                moveFocus(splitPane, 1);
            }
            else if (key == FOCUS_OUT_BACKWARD) {
                moveFocus(splitPane, -1);
            }
        }

        private void moveFocus(JSplitPane splitPane, int direction) {
            Container rootAncestor = splitPane.getFocusCycleRootAncestor();
            FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
            Component focusOn = (direction > 0) ?
                policy.getComponentAfter(rootAncestor, splitPane) :
                policy.getComponentBefore(rootAncestor, splitPane);
            HashSet focusFrom = new HashSet();
            if (splitPane.isAncestorOf(focusOn)) {
                do {
                    focusFrom.add(focusOn);
                    rootAncestor = focusOn.getFocusCycleRootAncestor();
                    policy = rootAncestor.getFocusTraversalPolicy();
                    focusOn = (direction > 0) ?
                        policy.getComponentAfter(rootAncestor, focusOn) :
                        policy.getComponentBefore(rootAncestor, focusOn);
                } while (splitPane.isAncestorOf(focusOn) &&
                         !focusFrom.contains(focusOn));
            }
            if ( focusOn!=null && !splitPane.isAncestorOf(focusOn) ) {
                focusOn.requestFocus();
            }
        }

        private void toggleFocus(JSplitPane splitPane) {
            Component left = splitPane.getLeftComponent();
            Component right = splitPane.getRightComponent();

            KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Component focus = manager.getFocusOwner();
            Component focusOn = getNextSide(splitPane, focus);
            if (focusOn != null) {
                // don't change the focus if the new focused component belongs
                // to the same splitpane and the same side
                if ( focus!=null &&
                     ( (SwingUtilities.isDescendingFrom(focus, left) &&
                        SwingUtilities.isDescendingFrom(focusOn, left)) ||
                       (SwingUtilities.isDescendingFrom(focus, right) &&
                        SwingUtilities.isDescendingFrom(focusOn, right)) ) ) {
                    return;
                }
                SwingUtilities2.compositeRequestFocus(focusOn);
            }
        }

        private Component getNextSide(JSplitPane splitPane, Component focus) {
            Component left = splitPane.getLeftComponent();
            Component right = splitPane.getRightComponent();
            Component next = null;
            if (focus!=null && SwingUtilities.isDescendingFrom(focus, left) &&
                right!=null) {
                next = getFirstAvailableComponent(right);
                if (next != null) {
                    return next;
                }
            }
            JSplitPane parentSplitPane = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, splitPane);
            if (parentSplitPane!=null) {
                // focus next side of the parent split pane
                next = getNextSide(parentSplitPane, focus);
            } else {
                next = getFirstAvailableComponent(left);
                if (next == null) {
                    next = getFirstAvailableComponent(right);
                }
            }
            return next;
        }

        private Component getFirstAvailableComponent(Component c) {
            if (c!=null && c instanceof JSplitPane) {
                JSplitPane sp = (JSplitPane)c;
                Component left = getFirstAvailableComponent(sp.getLeftComponent());
                if (left != null) {
                    c = left;
                } else {
                    c = getFirstAvailableComponent(sp.getRightComponent());
                }
            }
            return c;
        }
    }
}

class ModifiedBasicSplitPaneDivider extends Container
implements PropertyChangeListener
{
/**
 * Width or height of the divider based on orientation
 * BasicSplitPaneUI adds two to this.
 */
protected static final int ONE_TOUCH_SIZE = 18;
protected static final int ONE_TOUCH_OFFSET = 1;

/**
 * Handles mouse dragging message to do the actual dragging.
 */
protected DragController dragger;

/**
 * UI this instance was created from.
 */
protected ModifiedBasicSplitPaneUI splitPaneUI;

/**
 * Size of the divider.
 */
protected int dividerSize = 0; // default - SET TO 0???

/**
 * Divider that is used for noncontinuous layout mode.
 */
protected Component hiddenDivider;

/**
 * JSplitPane the receiver is contained in.
 */
protected JSplitPane splitPane;

/**
 * Handles mouse events from both this class, and the split pane.
 * Mouse events are handled for the splitpane since you want to be able
 * to drag when clicking on the border of the divider, which is not
 * drawn by the divider.
 */
protected MouseHandler mouseHandler;

/**
 * Orientation of the JSplitPane.
 */
protected int orientation;

/**
 * Button for quickly toggling the left component.
 */
protected JButton leftButton;

/**
 * Button for quickly toggling the right component.
 */
protected JButton rightButton;

/** Border. */
private Border border;

/**
 * Is the mouse over the divider?
 */
private boolean mouseOver;

private int oneTouchSize;
private int oneTouchOffset;

/**
 * If true the one touch buttons are centered on the divider.
 */
private boolean centerOneTouchButtons;

private Icon leftIcon,rightIcon,topIcon,bottomIcon;


/**
 * Creates an instance of BasicSplitPaneDivider. Registers this
 * instance for mouse events and mouse dragged events.
 */
public ModifiedBasicSplitPaneDivider(ModifiedBasicSplitPaneUI ui) {
    oneTouchSize = ONE_TOUCH_SIZE;
    oneTouchOffset = ONE_TOUCH_OFFSET;

    leftIcon=Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-first-gray");
    rightIcon=Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-last-gray");
    topIcon=Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-top-gray");
    bottomIcon=Views.getIconFactory().getIcon(IconSize.SMALL, "actions", "go-bottom-gray");
    
    centerOneTouchButtons = DefaultLookup.getBoolean(ui.getSplitPane(),
             ui, "SplitPane.centerOneTouchButtons", true);
    setLayout(new DividerLayout());
    setBasicSplitPaneUI(ui);
    orientation = splitPane.getOrientation();
    setCursor((orientation == JSplitPane.HORIZONTAL_SPLIT) ?
              Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) :
              Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
    setBackground(UIManager.getColor("SplitPane.background"));

}

private void revalidate() {
    invalidate();
    if (splitPane != null) {
        splitPane.revalidate();
    }
}

/**
 * Sets the SplitPaneUI that is using the receiver.
 */
public void setBasicSplitPaneUI(ModifiedBasicSplitPaneUI modifiedBasicSplitPaneUI) {
    if (splitPane != null) {
        splitPane.removePropertyChangeListener(this);
       if (mouseHandler != null) {
           splitPane.removeMouseListener(mouseHandler);
           splitPane.removeMouseMotionListener(mouseHandler);
           removeMouseListener(mouseHandler);
           removeMouseMotionListener(mouseHandler);
           mouseHandler = null;
       }
    }
    splitPaneUI = modifiedBasicSplitPaneUI;
    if (modifiedBasicSplitPaneUI != null) {
        splitPane = modifiedBasicSplitPaneUI.getSplitPane();
        if (splitPane != null) {
            if (mouseHandler == null) mouseHandler = new MouseHandler();
            splitPane.addMouseListener(mouseHandler);
            splitPane.addMouseMotionListener(mouseHandler);
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            splitPane.addPropertyChangeListener(this);
            if (splitPane.isOneTouchExpandable()) {
                oneTouchExpandableChanged();
            }
        }
    }
    else {
        splitPane = null;
    }
}


/**
 * Returns the <code>SplitPaneUI</code> the receiver is currently
 * in.
 */
public ModifiedBasicSplitPaneUI getBasicSplitPaneUI() {
    return splitPaneUI;
}


/**
 * Sets the size of the divider to <code>newSize</code>. That is
 * the width if the splitpane is <code>HORIZONTAL_SPLIT</code>, or
 * the height of <code>VERTICAL_SPLIT</code>.
 */
public void setDividerSize(int newSize) {
    dividerSize = newSize;
}


/**
 * Returns the size of the divider, that is the width if the splitpane
 * is HORIZONTAL_SPLIT, or the height of VERTICAL_SPLIT.
 */
public int getDividerSize() {
    return dividerSize;
}


/**
 * Sets the border of this component.
 * @since 1.3
 */
public void setBorder(Border border) {
    Border         oldBorder = this.border;

    this.border = border;
}

/**
 * Returns the border of this component or null if no border is
 * currently set.
 *
 * @return the border object for this component
 * @see #setBorder
 * @since 1.3
 */
public Border getBorder() {
    return border;
}

/**
 * If a border has been set on this component, returns the
 * border's insets, else calls super.getInsets.
 *
 * @return the value of the insets property.
 * @see #setBorder
 */
public Insets getInsets() {
    Border    border = getBorder();

    if (border != null) {
        return border.getBorderInsets(this);
    }
    return super.getInsets();
}

/**
 * Sets whether or not the mouse is currently over the divider.
 *
 * @param mouseOver whether or not the mouse is currently over the divider
 * @since 1.5
 */
protected void setMouseOver(boolean mouseOver) {
    this.mouseOver = mouseOver;
}

/**
 * Returns whether or not the mouse is currently over the divider
 *
 * @return whether or not the mouse is currently over the divider
 * @since 1.5
 */
public boolean isMouseOver() {
    return mouseOver;
}

/**
 * Returns dividerSize x dividerSize
 */
public Dimension getPreferredSize() {
    // Ideally this would return the size from the layout manager,
    // but that could result in the layed out size being different from
    // the dividerSize, which may break developers as well as
    // BasicSplitPaneUI.
    if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
        return new Dimension(getDividerSize(), 1);
    }
    return new Dimension(1, getDividerSize());
}

/**
 * Returns dividerSize x dividerSize
 */
public Dimension getMinimumSize() {
    return getPreferredSize();
}


/**
 * Property change event, presumably from the JSplitPane, will message
 * updateOrientation if necessary.
 */
public void propertyChange(PropertyChangeEvent e) {
    if (e.getSource() == splitPane) {
        if (e.getPropertyName() == JSplitPane.ORIENTATION_PROPERTY) {
            orientation = splitPane.getOrientation();
            setCursor((orientation == JSplitPane.HORIZONTAL_SPLIT) ?
                      Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) :
                      Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            revalidate();
        }
        else if (e.getPropertyName() == JSplitPane.
                  ONE_TOUCH_EXPANDABLE_PROPERTY) {
            oneTouchExpandableChanged();
        }
    }
}


/**
 * Paints the divider.
 */
public void paint(Graphics g) {
  super.paint(g);
  Border   border = getBorder();
  if (border != null) {
      Dimension size = getSize();
      border.paintBorder(this, g, 0, 0, size.width, size.height);
  }
  
  Dimension size = getSize();
  g.setColor(this.getBackground());
  g.draw3DRect(size.width/2-(size.width/6), size.height/2-(size.height/6),size.width/3,size.height/3,false);
}


/**
 * Messaged when the oneTouchExpandable value of the JSplitPane the
 * receiver is contained in changes. Will create the
 * <code>leftButton</code> and <code>rightButton</code> if they
 * are null. invalidates the receiver as well.
 */
protected void oneTouchExpandableChanged() {
    if (!DefaultLookup.getBoolean(splitPane, splitPaneUI,
                       "SplitPane.supportsOneTouchButtons", true)) {
        // Look and feel doesn't want to support one touch buttons, bail.
        return;
    }
    if (splitPane.isOneTouchExpandable() &&
        leftButton == null &&
        rightButton == null) {
        /* Create the left button and add an action listener to
           expand/collapse it. */
        leftButton = createLeftOneTouchButton();
        if (leftButton != null)
            leftButton.addActionListener(new OneTouchActionHandler(true));


        /* Create the right button and add an action listener to
           expand/collapse it. */
        rightButton = createRightOneTouchButton();
        if (rightButton != null)
            rightButton.addActionListener(new OneTouchActionHandler
                (false));

        if (leftButton != null && rightButton != null) {
            add(leftButton);
            add(rightButton);
        }
    }
    revalidate();
}


/**
 * Creates and return an instance of JButton that can be used to
 * collapse the left component in the split pane.
 */
protected JButton createLeftOneTouchButton() {
    JButton b = new JButton() {
        public void setBorder(Border border) {
        }
        public void paint(Graphics g) {
            if (splitPane != null) {
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                	leftIcon.paintIcon(this, g, 1, 1);
                }
                else {
                	topIcon.paintIcon(this, g, 1, 1);
                }
            }
        }
        public boolean isFocusTraversable() {
            return false;
        }
    };
    b.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
    b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setRequestFocusEnabled(false);
    return b;
}


/**
 * Creates and return an instance of JButton that can be used to
 * collapse the right component in the split pane.
 */
protected JButton createRightOneTouchButton() {
    JButton b = new JButton() {
        public void setBorder(Border border) {
        }
        public void paint(Graphics g) {
            if (splitPane != null) {
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                	rightIcon.paintIcon(this, g, 1, 1);
                }
                else {
                	bottomIcon.paintIcon(this, g, 1, 1);
                }
            }
        }
        public boolean isFocusTraversable() {
            return false;
        }
    };
    b.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
    b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setRequestFocusEnabled(false);
    return b;
}


/**
 * Message to prepare for dragging. This messages the BasicSplitPaneUI
 * with startDragging.
 */
protected void prepareForDragging() {
    splitPaneUI.startDragging();
}


/**
 * Messages the BasicSplitPaneUI with dragDividerTo that this instance
 * is contained in.
 */
protected void dragDividerTo(int location) {
    splitPaneUI.dragDividerTo(location);
}


/**
 * Messages the BasicSplitPaneUI with finishDraggingTo that this instance
 * is contained in.
 */
protected void finishDraggingTo(int location) {
    splitPaneUI.finishDraggingTo(location);
}


/**
 * MouseHandler is responsible for converting mouse events
 * (released, dragged...) into the appropriate DragController
 * methods.
 * <p>
 */
protected class MouseHandler extends MouseAdapter
        implements MouseMotionListener
{
    /**
     * Starts the dragging session by creating the appropriate instance
     * of DragController.
     */
    public void mousePressed(MouseEvent e) {
        if ((e.getSource() == ModifiedBasicSplitPaneDivider.this ||
             e.getSource() == splitPane) &&
            dragger == null &&splitPane.isEnabled()) {
            Component            newHiddenDivider = splitPaneUI.
                                 getNonContinuousLayoutDivider();

            if (hiddenDivider != newHiddenDivider) {
                if (hiddenDivider != null) {
                    hiddenDivider.removeMouseListener(this);
                    hiddenDivider.removeMouseMotionListener(this);
                }
                hiddenDivider = newHiddenDivider;
                if (hiddenDivider != null) {
                    hiddenDivider.addMouseMotionListener(this);
                    hiddenDivider.addMouseListener(this);
                }
            }
            if (splitPane.getLeftComponent() != null &&
                splitPane.getRightComponent() != null) {
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    dragger = new DragController(e);
                }
                else {
                    dragger = new VerticalDragController(e);
                }
                if (!dragger.isValid()) {
                    dragger = null;
                }
                else {
                    prepareForDragging();
                    dragger.continueDrag(e);
                }
            }
            e.consume();
        }
    }


    /**
     * If dragger is not null it is messaged with completeDrag.
     */
    public void mouseReleased(MouseEvent e) {
        if (dragger != null) {
            if (e.getSource() == splitPane) {
                dragger.completeDrag(e.getX(), e.getY());
            }
            else if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
                Point   ourLoc = getLocation();

                dragger.completeDrag(e.getX() + ourLoc.x,
                                     e.getY() + ourLoc.y);
            }
            else if (e.getSource() == hiddenDivider) {
                Point   hDividerLoc = hiddenDivider.getLocation();
                int     ourX = e.getX() + hDividerLoc.x;
                int     ourY = e.getY() + hDividerLoc.y;

                dragger.completeDrag(ourX, ourY);
            }
            dragger = null;
            e.consume();
        }
    }


    //
    // MouseMotionListener
    //

    /**
     * If dragger is not null it is messaged with continueDrag.
     */
    public void mouseDragged(MouseEvent e) {
        if (dragger != null) {
            if (e.getSource() == splitPane) {
                dragger.continueDrag(e.getX(), e.getY());
            }
            else if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
                Point   ourLoc = getLocation();

                dragger.continueDrag(e.getX() + ourLoc.x,
                                     e.getY() + ourLoc.y);
            }
            else if (e.getSource() == hiddenDivider) {
                Point   hDividerLoc = hiddenDivider.getLocation();
                int     ourX = e.getX() + hDividerLoc.x;
                int     ourY = e.getY() + hDividerLoc.y;

                dragger.continueDrag(ourX, ourY);
            }
            e.consume();
        }
    }


    /**
     *  Resets the cursor based on the orientation.
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e MouseEvent describing the details of the enter event.
     * @since 1.5
     */
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
            setMouseOver(true);
        }
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e MouseEvent describing the details of the exit event.
     * @since 1.5
     */
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
            setMouseOver(false);
        }
    }
}


/**
 * Handles the events during a dragging session for a
 * HORIZONTAL_SPLIT oriented split pane. This continually
 * messages <code>dragDividerTo</code> and then when done messages
 * <code>finishDraggingTo</code>. When an instance is created it should be
 * messaged with <code>isValid</code> to insure that dragging can happen
 * (dragging won't be allowed if the two views can not be resized).
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 */
protected class DragController
{
    /**
     * Initial location of the divider.
     */
    int initialX;

    /**
     * Maximum and minimum positions to drag to.
     */
    int maxX, minX;

    /**
     * Initial location the mouse down happened at.
     */
    int offset;


    protected DragController(MouseEvent e) {
        JSplitPane  splitPane = splitPaneUI.getSplitPane();
        Component   leftC = splitPane.getLeftComponent();
        Component   rightC = splitPane.getRightComponent();

        initialX = getLocation().x;
        if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
            offset = e.getX();
        }
        else { // splitPane
            offset = e.getX() - initialX;
        }
        if (leftC == null || rightC == null || offset < -1 ||
            offset >= getSize().width) {
            // Don't allow dragging.
            maxX = -1;
        }
        else {
            Insets      insets = splitPane.getInsets();

            if (leftC.isVisible()) {
                minX = leftC.getMinimumSize().width;
                if (insets != null) {
                    minX += insets.left;
                }
            }
            else {
                minX = 0;
            }
            if (rightC.isVisible()) {
                int right = (insets != null) ? insets.right : 0;
                maxX = Math.max(0, splitPane.getSize().width -
                                (getSize().width + right) -
                                rightC.getMinimumSize().width);
            }
            else {
                int right = (insets != null) ? insets.right : 0;
                maxX = Math.max(0, splitPane.getSize().width -
                                (getSize().width + right));
            }
            if (maxX < minX) minX = maxX = 0;
        }
    }


    /**
     * Returns true if the dragging session is valid.
     */
    protected boolean isValid() {
        return (maxX > 0);
    }


    /**
     * Returns the new position to put the divider at based on
     * the passed in MouseEvent.
     */
    protected int positionForMouseEvent(MouseEvent e) {
        int newX = (e.getSource() == ModifiedBasicSplitPaneDivider.this) ?
                    (e.getX() + getLocation().x) : e.getX();

        newX = Math.min(maxX, Math.max(minX, newX - offset));
        return newX;
    }


    /**
     * Returns the x argument, since this is used for horizontal
     * splits.
     */
    protected int getNeededLocation(int x, int y) {
        int newX;

        newX = Math.min(maxX, Math.max(minX, x - offset));
        return newX;
    }


    protected void continueDrag(int newX, int newY) {
        dragDividerTo(getNeededLocation(newX, newY));
    }


    /**
     * Messages dragDividerTo with the new location for the mouse
     * event.
     */
    protected void continueDrag(MouseEvent e) {
        dragDividerTo(positionForMouseEvent(e));
    }


    protected void completeDrag(int x, int y) {
        finishDraggingTo(getNeededLocation(x, y));
    }


    /**
     * Messages finishDraggingTo with the new location for the mouse
     * event.
     */
    protected void completeDrag(MouseEvent e) {
        finishDraggingTo(positionForMouseEvent(e));
    }
} // End of BasicSplitPaneDivider.DragController


/**
 * Handles the events during a dragging session for a
 * VERTICAL_SPLIT oriented split pane. This continually
 * messages <code>dragDividerTo</code> and then when done messages
 * <code>finishDraggingTo</code>. When an instance is created it should be
 * messaged with <code>isValid</code> to insure that dragging can happen
 * (dragging won't be allowed if the two views can not be resized).
 */
protected class VerticalDragController extends DragController
{
    /* DragControllers ivars are now in terms of y, not x. */
    protected VerticalDragController(MouseEvent e) {
        super(e);
        JSplitPane splitPane = splitPaneUI.getSplitPane();
        Component  leftC = splitPane.getLeftComponent();
        Component  rightC = splitPane.getRightComponent();

        initialX = getLocation().y;
        if (e.getSource() == ModifiedBasicSplitPaneDivider.this) {
            offset = e.getY();
        }
        else {
            offset = e.getY() - initialX;
        }
        if (leftC == null || rightC == null || offset < -1 ||
            offset > getSize().height) {
            // Don't allow dragging.
            maxX = -1;
        }
        else {
            Insets     insets = splitPane.getInsets();

            if (leftC.isVisible()) {
                minX = leftC.getMinimumSize().height;
                if (insets != null) {
                    minX += insets.top;
                }
            }
            else {
                minX = 0;
            }
            if (rightC.isVisible()) {
                int    bottom = (insets != null) ? insets.bottom : 0;

                maxX = Math.max(0, splitPane.getSize().height -
                                (getSize().height + bottom) -
                                rightC.getMinimumSize().height);
            }
            else {
                int    bottom = (insets != null) ? insets.bottom : 0;

                maxX = Math.max(0, splitPane.getSize().height -
                                (getSize().height + bottom));
            }
            if (maxX < minX) minX = maxX = 0;
        }
    }


    /**
     * Returns the y argument, since this is used for vertical
     * splits.
     */
    protected int getNeededLocation(int x, int y) {
        int newY;

        newY = Math.min(maxX, Math.max(minX, y - offset));
        return newY;
    }


    /**
     * Returns the new position to put the divider at based on
     * the passed in MouseEvent.
     */
    protected int positionForMouseEvent(MouseEvent e) {
        int newY = (e.getSource() == ModifiedBasicSplitPaneDivider.this) ?
                    (e.getY() + getLocation().y) : e.getY();


        newY = Math.min(maxX, Math.max(minX, newY - offset));
        return newY;
    }
} // End of BasicSplitPaneDividier.VerticalDragController


/**
 * Used to layout a <code>BasicSplitPaneDivider</code>.
 * Layout for the divider
 * involves appropriately moving the left/right buttons around.
 * <p>
 */
protected class DividerLayout implements LayoutManager
{
    public void layoutContainer(Container c) {
        if (leftButton != null && rightButton != null &&
            c == ModifiedBasicSplitPaneDivider.this) {
            if (splitPane.isOneTouchExpandable()) {
                Insets insets = getInsets();

                if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    int extraX = (insets != null) ? insets.left : 0;
                    int blockSize = getHeight();

                    if (insets != null) {
                        blockSize -= (insets.top + insets.bottom);
                        blockSize = Math.max(blockSize, 0);
                    }
                    blockSize = Math.min(blockSize, oneTouchSize);

                    int y = (c.getSize().height - blockSize) / 2;

                    if (!centerOneTouchButtons) {
                        y = (insets != null) ? insets.top : 0;
                        extraX = 0;
                    }
                    leftButton.setBounds(extraX + oneTouchOffset, y,
                                         blockSize * 2, blockSize);
                    rightButton.setBounds(extraX + oneTouchOffset +
                                          oneTouchSize * 2, y,
                                          blockSize * 2, blockSize);
                }
                else {
                    int extraY = (insets != null) ? insets.top : 0;
                    int blockSize = getWidth();

                    if (insets != null) {
                        blockSize -= (insets.left + insets.right);
                        blockSize = Math.max(blockSize, 0);
                    }
                    blockSize = Math.min(blockSize, oneTouchSize);

                    int x = (c.getSize().width - blockSize) / 2;

                    if (!centerOneTouchButtons) {
                        x = (insets != null) ? insets.left : 0;
                        extraY = 0;
                    }

                    leftButton.setBounds(x, extraY + oneTouchOffset,
                                         blockSize, blockSize * 2);
                    rightButton.setBounds(x, extraY + oneTouchOffset +
                                          oneTouchSize * 2, blockSize,
                                          blockSize * 2);
                }
            }
            else {
                leftButton.setBounds(-5, -5, 1, 1);
                rightButton.setBounds(-5, -5, 1, 1);
            }
        }
    }


    public Dimension minimumLayoutSize(Container c) {
        // NOTE: This isn't really used, refer to
        // BasicSplitPaneDivider.getPreferredSize for the reason.
        // I leave it in hopes of having this used at some point.
        if (c != ModifiedBasicSplitPaneDivider.this || splitPane == null) {
            return new Dimension(0,0);
        }
        Dimension buttonMinSize = null;

        if (splitPane.isOneTouchExpandable() && leftButton != null) {
            buttonMinSize = leftButton.getMinimumSize();
        }

        Insets insets = getInsets();
        int width = getDividerSize();
        int height = width;

        if (orientation == JSplitPane.VERTICAL_SPLIT) {
            if (buttonMinSize != null) {
                int size = buttonMinSize.height;
                if (insets != null) {
                    size += insets.top + insets.bottom;
                }
                height = Math.max(height, size);
            }
            width = 1;
        }
        else {
            if (buttonMinSize != null) {
                int size = buttonMinSize.width;
                if (insets != null) {
                    size += insets.left + insets.right;
                }
                width = Math.max(width, size);
            }
            height = 1;
        }
        return new Dimension(width, height);
    }


    public Dimension preferredLayoutSize(Container c) {
        return minimumLayoutSize(c);
    }


    public void removeLayoutComponent(Component c) {}

    public void addLayoutComponent(String string, Component c) {}
} // End of class BasicSplitPaneDivider.DividerLayout


/**
 * Listeners installed on the one touch expandable buttons.
 */
private class OneTouchActionHandler implements ActionListener {
    /** True indicates the resize should go the minimum (top or left)
     * vs false which indicates the resize should go to the maximum.
     */
    private boolean toMinimum;

    OneTouchActionHandler(boolean toMinimum) {
        this.toMinimum = toMinimum;
    }

    public void actionPerformed(ActionEvent e) {
        Insets  insets = splitPane.getInsets();
        int     lastLoc = splitPane.getLastDividerLocation();
        int     currentLoc = splitPaneUI.getDividerLocation(splitPane);
        int     newLoc;

        // We use the location from the UI directly, as the location the
        // JSplitPane itself maintains is not necessarly correct.
        if (toMinimum) {
            if (orientation == JSplitPane.VERTICAL_SPLIT) {
                if (currentLoc >= (splitPane.getHeight() -
                                   insets.bottom - getHeight())) {
                    int maxLoc = splitPane.getMaximumDividerLocation();
                    newLoc = Math.min(lastLoc, maxLoc);
                    splitPaneUI.setKeepHidden(false);
                }
                else {
                    newLoc = insets.top;
                    splitPaneUI.setKeepHidden(true);
                }
            }
            else {
                if (currentLoc >= (splitPane.getWidth() -
                                   insets.right - getWidth())) {
                    int maxLoc = splitPane.getMaximumDividerLocation();
                    newLoc = Math.min(lastLoc, maxLoc);
                    splitPaneUI.setKeepHidden(false);
                }
                else {
                    newLoc = insets.left;
                    splitPaneUI.setKeepHidden(true);
                }
            }
        }
        else {
            if (orientation == JSplitPane.VERTICAL_SPLIT) {
                if (currentLoc == insets.top) {
                    int maxLoc = splitPane.getMaximumDividerLocation();
                    newLoc = Math.min(lastLoc, maxLoc);
                    splitPaneUI.setKeepHidden(false);
                }
                else {
                    newLoc = splitPane.getHeight() - getHeight() -
                             insets.top;
                    splitPaneUI.setKeepHidden(true);
                }
            }
            else {
                if (currentLoc == insets.left) {
                    int maxLoc = splitPane.getMaximumDividerLocation();
                    newLoc = Math.min(lastLoc, maxLoc);
                    splitPaneUI.setKeepHidden(false);
                }
                else {
                    newLoc = splitPane.getWidth() - getWidth() -
                             insets.left;
                    splitPaneUI.setKeepHidden(true);
                }
            }
        }
        if (currentLoc != newLoc) {
            splitPane.setDividerLocation(newLoc);
            // We do this in case the dividers notion of the location
            // differs from the real location.
            splitPane.setLastDividerLocation(currentLoc);
        }
    }
} // End of class BasicSplitPaneDivider.LeftActionListener
}
