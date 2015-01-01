/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.impl.viewers;


import aves.dpt.intf.viewers.AvesViewer;

import com.jogamp.common.nio.Buffers;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.util.webview.WebView;
import gov.nasa.worldwind.util.webview.WebViewFactory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.WWTexture;
import gov.nasa.worldwind.util.BasicTextDecoder;
import gov.nasa.worldwind.util.HotSpot;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.OGLStackHandler;
import gov.nasa.worldwind.util.OGLUtil;
import gov.nasa.worldwind.util.TextDecoder;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.webview.BasicWebViewFactory;
import gov.nasa.worldwind.util.webview.WebResourceResolver;

import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;


/**
 * 
 * @author svlieffe 09/14/2014 <p>
 * modified gov.nasa.worldwind.render.AbstractBrowserBalloon.java from dcollins 
 */
public class AvesBrowser extends AVListImpl implements OrderedRenderable, HotSpot, WWObject {
    protected WebView webView;
    protected Point screenPoint = new Point(0, 0);
    protected Dimension screenSize;
    protected boolean alwaysOnTop = false;
    protected boolean pickEnabled = true;
    String htmlString = null;
    private AvesViewer avesViewer;
    protected TextDecoder textDecoder = new BasicTextDecoder();
    protected String text;
    Rectangle webViewRect;
    Rectangle screenRect;
    /**
     * The size of the WebView's HTML content size, in pixels. This is the size that the WebView can be displayed at
     * without the need for scroll bars. May be <code>null</code> or <code>(0, 0)</code>, indicating that the WebView's
     * HTML content size is unknown. Initially <code>null</code>.
     */
    protected Dimension webViewContentSize;
    protected OGLStackHandler osh = new OGLStackHandler();
    /** The layer active during the most recent pick pass. */
    protected Layer pickLayer;
    /** The screen coordinate of the last <code>SelectEvent</code> sent to this balloon's <code>select</code> method. */
    protected Point lastPickPoint;
    /** Support for setting up and restoring picking state, and resolving the picked object. */
    protected PickSupport pickSupport = new PickSupport();
    FloatBuffer tbuf;
    IntBuffer vbuf;
    WWTexture texture;
    Object delegateOwner;
    /**
     * Indicates the object used to resolve relative resource paths in this browser balloon's HTML content. May be one
     * of the following: <code>{@link gov.nasa.worldwind.util.webview.WebResourceResolver}</code>, <code>{@link
     * java.net.URL}</code>, <code>{@link String}</code> containing a valid URL description, or <code>null</code> to
     * specify that relative paths should be interpreted as unresolved references. Initially <code>null</code>.
     */
    protected Object resourceResolver;
    /** Identifies the time when the balloon text was updated. Initially -1. */
    protected long textUpdateTime = -1;
    /** Identifies the frame used to calculate the balloon's active attributes and points. */
    protected long frameTimeStamp = -1;
    /** Identifies the frame used to update the WebView's state. */
    protected long webViewTimeStamp = -1;
    /** The location of the balloon's content frame relative to the balloon's screen point in the viewport. */
    protected Point screenOffset;
    /**
     * Denotes whether or not an attempt at WebView creation failed. When <code>true</code> the balloon does not perform
     * subsequent attempts to create the WebView. Initially <code>false</code>.
     */
    protected boolean webViewCreationFailed;
    protected BalloonAttributes attributes;
    protected BalloonAttributes highlightAttributes;
    protected BalloonAttributes activeAttributes = new BasicBalloonAttributes(); // re-determined each frame
    /** The attributes used if attributes are not specified. */
    protected static final BalloonAttributes defaultAttributes;
    static {
        defaultAttributes = new BasicBalloonAttributes();
    }
    
    protected static final String DEFAULT_WEB_VIEW_FACTORY = BasicWebViewFactory.class.getName();

    public AvesBrowser(String htmlStr, int width, int height, AvesViewer avesViewer) {

        this.setText(htmlStr);
        this.htmlString = htmlStr;
        this.avesViewer = avesViewer;
        this.screenSize = new Dimension(width, height);
        webViewRect = new Rectangle(screenSize);
        screenRect = new Rectangle(screenSize);
        tbuf = this.makeTexRectangle(0.0f, 0.0f, 1.0f, 1.0f);
        vbuf = this.makeVtxRectangle(0, 0, screenSize.width, screenSize.height);        
    }
    
    /**
     * Disposes the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>. This does nothing
     * if the balloon is already disposed.
     */
    public void dispose() {
        this.disposeWebView();
    }

        /**
     * The property change listener for <em>this</em> instance.
     * Receives property change notifications that this instance has registered with other property change notifiers.
     * @param propertyChangeEvent the event
     * @throws IllegalArgumentException if <code>propertyChangeEvent</code> is null
     */
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent == null) {
            String msg = Logging.getMessage("nullValue.PropertyChangeEventIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // Notify all *my* listeners of the change that I caught
        super.firePropertyChange(propertyChangeEvent);
    }

    /** Empty implementation of MessageListener. */
    public void onMessage(Message message) {
        // Empty implementation
    }

    @Override
    public void render(DrawContext dc) {
        if (dc.isOrderedRenderingMode()) {
            this.drawOrderedRenderable(dc);
        } else {
            this.makeOrderedRenderable(dc);
        }

    }

    protected void drawOrderedRenderable(DrawContext dc) {
        this.beginDrawing(dc);
        try {
            this.doDrawOrderedRenderable(dc);
        } finally {
            this.endDrawing(dc);
        }
    }

    protected void makeOrderedRenderable(DrawContext dc) {
        // Update the balloon's WebView to be current with the BrowserBalloon's properties. This must be done after
        // updating the render state; this balloon's active attributes are applied to the WebView. Re-use WebView state
        // already calculated this frame.
        if (dc.getFrameTimeStamp() != this.webViewTimeStamp) {
            this.updateWebView(dc);
            this.webViewTimeStamp = dc.getFrameTimeStamp();
        }

        if (dc.isPickingMode()) {
            this.pickLayer = dc.getCurrentLayer();
        }

        dc.addOrderedRenderable(this);
    }

    protected void beginDrawing(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();

        int attrMask =
                GL2.GL_COLOR_BUFFER_BIT // For alpha enable, blend enable, alpha func, blend func.
                | GL2.GL_CURRENT_BIT // For current color
                | GL2.GL_DEPTH_BUFFER_BIT // For depth test enable/disable, depth func, depth mask.
                | GL2.GL_LINE_BIT // For line smooth enable, line stipple enable, line width, line stipple factor,
                // line stipple pattern.
                | GL2.GL_POLYGON_BIT // For polygon mode.
                | GL2.GL_VIEWPORT_BIT; // For depth range.

        this.osh.clear(); // Reset the stack handler's internal state.
        this.osh.pushAttrib(gl, attrMask);
        this.osh.pushClientAttrib(gl, GL2.GL_CLIENT_VERTEX_ARRAY_BIT); // For vertex array enable, vertex array pointers.
        this.osh.pushProjectionIdentity(gl);
        // The browser balloon is drawn using a parallel projection sized to fit the viewport.
        gl.glOrtho(0d, dc.getView().getViewport().width, 0d, dc.getView().getViewport().height, -1d, 1d);
        this.osh.pushTextureIdentity(gl);
        this.osh.pushModelviewIdentity(gl);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY); // All drawing uses vertex arrays.

        if (!dc.isPickingMode()) {
            gl.glEnable(GL2.GL_BLEND); // Enable interior and outline alpha blending when not picking.
            OGLUtil.applyBlending(gl, false);
        }
    }

    protected void endDrawing(DrawContext dc) {
        this.osh.pop(dc.getGL().getGL2());
    }

    protected void doDrawOrderedRenderable(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();

        if (dc.isPickingMode()) {
            // Set up the pick color used during interior and outline rendering.
            Color pickColor = dc.getUniquePickColor();
            this.pickSupport.addPickableObject(this.createPickedObject(dc, pickColor));
            gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
        }

        // Translate to the balloon's screen origin. Use integer coordinates to ensure that the WebView texels are
        // aligned exactly with screen pixels.
        gl.glTranslatef(this.screenRect.x, this.screenRect.y, 0);

        this.drawFrame(dc);

        if (this.isDrawLinks(dc)) {
            this.drawLinks(dc);
        }

    }

    /** {@inheritDoc} */
    public void setActive(boolean active) {
        if (this.webView != null) {
            this.webView.setActive(active);
        }
    }

    /** {@inheritDoc} */
    public boolean isActive() {
        return (this.webView != null) && this.webView.isActive();
    }

    /**
     * Indicates the object used to resolve relative resource paths in this browser balloon's HTML content.
     *
     * @return the object used to resolve relative resource paths in HTML content. One of the following: <code>{@link
     *         gov.nasa.worldwind.util.webview.WebResourceResolver}</code>, <code>{@link java.net.URL}</code>,
     *         <code>{@link String}</code> containing a valid URL description, or <code>null</code> to indicate that
     *         relative paths are interpreted as unresolved references.
     *
     * @see #setResourceResolver(Object)
     */
    public Object getResourceResolver() {
        return this.resourceResolver;
    }

    /**
     * Specifies a the object to use when resolving relative resource paths in this browser balloon's HTML content. The
     * <code>resourceResolver</code> may be one of the following:
     * <p>
     * <ul> <li>a <code>{@link gov.nasa.worldwind.util.webview.WebResourceResolver}</code></li> <li>a <code>{@link
     * java.net.URL}</code></li> <li>a <code>{@link String}</code> containing a valid URL description</li> </ul>
     * <p>
     * If the <code>resourceResolver</code> is <code>null</code> or is not one of the recognized types, this browser
     * balloon interprets relative resource paths as unresolved references.
     *
     * @param resourceResolver the object to use when resolving relative resource paths in HTML content. May be one of
     *                         the following: <code>{@link gov.nasa.worldwind.util.webview.WebResourceResolver}</code>,
     *                         <code>{@link java.net.URL}</code>, <code>{@link String}</code> containing a valid URL
     *                         description, or <code>null</code> to specify that relative paths should be interpreted as
     *                         unresolved references.
     *
     * @see #getResourceResolver()
     */
    public void setResourceResolver(Object resourceResolver) {
        this.resourceResolver = resourceResolver;

        // Setting a new resource resolver may change how the WebView content is rendered. Set the textUpdate time to
        // ensure that the WebView content will be reset on the next frame.
        this.textUpdateTime = -1;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.getTextDecoder().setText(text);
    }

    /** {@inheritDoc} */
    public TextDecoder getTextDecoder() {
        return this.textDecoder;
    }

    /** {@inheritDoc} */
    public void setTextDecoder(TextDecoder decoder) {
        if (decoder == null) {
            String message = Logging.getMessage("nullValue.TextDecoderIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.textDecoder = decoder;
        this.textDecoder.setText(this.getText());
    }

    protected void updateWebView(DrawContext dc) {
        // Attempt to create the balloon's WebView.
        if (this.webView == null) {
            this.makeWebView(dc, this.webViewRect.getSize());
            // Exit immediately if WebView creation failed.
            if (this.webView == null) {
                return;
            }
        }

        // The WebView's frame size and background color can change each frame. Synchronize the WebView's background
        // color and frame size with the desired values before attempting to use the WebView's texture. The WebView
        // avoids doing unnecessary work when the same frame size or background color is specified.
        this.webView.setFrameSize(this.webViewRect.getSize());

        // Update the WebView's text content each time the balloon's decoded string changes. We update the text even if
        // the user has navigated to a page other than the balloon's text content. This ensures that any changes the
        // application makes to the decoded text are reflected in the browser balloon's content. If we ignore those
        // changes when the user navigates to another page, the application cannot retain control over the balloon's
        // content.
        if (this.getTextDecoder().getLastUpdateTime() != this.textUpdateTime) {
            this.setWebViewContent();
            this.textUpdateTime = this.getTextDecoder().getLastUpdateTime();
        }
    }

    protected void setWebViewContent() {
        String text = this.getTextDecoder().getDecodedText();
        Object resourceResolver = this.getResourceResolver();

        if (resourceResolver instanceof WebResourceResolver) {
            this.webView.setHTMLString(text, (WebResourceResolver) resourceResolver);
        } else if (resourceResolver instanceof URL) {
            this.webView.setHTMLString(text, (URL) resourceResolver);
        } else if (resourceResolver instanceof String) {
            // If the string is not a valid URL, then makeURL returns null and the WebView treats any relative paths as
            // unresolved references.
            URL url = WWIO.makeURL((String) resourceResolver);

            if (url == null) {
                Logging.logger().warning(Logging.getMessage("generic.URIInvalid", resourceResolver));
            }

            this.webView.setHTMLString(text, url);
        } else {
            if (resourceResolver != null) {
                Logging.logger().warning(Logging.getMessage("generic.UnrecognizedResourceResolver", resourceResolver));
            }
            this.webView.setHTMLString(text);
        }
    }

    protected void makeWebView(DrawContext dc, Dimension frameSize) {

        if (this.webView != null || this.webViewCreationFailed) {
            return;
        }

        try {
            // Attempt to get the WebViewFactory class name from configuration. Fall back on the BrowserBalloon's
            // default factory if the configuration does not specify a one.
            String className = Configuration.getStringValue(AVKey.WEB_VIEW_FACTORY, DEFAULT_WEB_VIEW_FACTORY);
            WebViewFactory factory = (WebViewFactory) WorldWind.createComponent(className);
            this.webView = factory.createWebView(frameSize);
        } catch (Throwable t) {
            String message = Logging.getMessage("WebView.ExceptionCreatingWebView", t);
            Logging.logger().severe(message);

            dc.addRenderingException(t);

            // Set flag to prevent retrying the web view creation. We assume that if this fails once it will continue to
            // fail.
            this.webViewCreationFailed = true;
        }

        // Configure the balloon to forward the WebView's property change events to its listeners. AND set the html ref and text (SVL 05262012)
        if (this.webView != null) {
            this.webView.addPropertyChangeListener(this);
        }
    }

    protected void disposeWebView() {
        if (this.webView == null) {
            return;
        }

        this.webView.removePropertyChangeListener(this);
        this.webView.dispose();
        this.webView = null;
        this.textUpdateTime = -1;
        this.webViewContentSize = null;
    }

/*    protected void determineWebViewContentSize() {
        // Update the WebView's HTML content size when the WebView is non-null and has not navigated to a another page
        // (indicated by a non-null URL). The latter case indicates that the WebView is not displaying this balloon's
        // text. We avoid updating the content size in this case to ensure that the balloon's size always fits the
        // balloon's text, and not content the user navigates to. Fitting the balloon's size to the WebView's current
        // content causes the balloon to abruptly change size as the user navigates. Note that the content size may be
        // null or (0, 0), indicating that the WebView does not know its content size. The balloon handles this by
        // falling back to a default content size.
        if (this.webView != null && this.webView.getContentURL() == null) {
            this.webViewContentSize = this.webView.getContentSize();
        }
    }
*/
    public void pick(DrawContext dc, Point pickPoint) {
        // This method is called only when ordered renderables are being drawn.
        // Arg checked within call to render.

        if (!this.isPickEnabled()) {
            return;
        }

        this.pickSupport.clearPickList();
        try {
            this.pickSupport.beginPicking(dc);
            this.render(dc);
        } finally {
            this.pickSupport.endPicking(dc);
            this.pickSupport.resolvePick(dc, pickPoint, this.pickLayer);
        }
    }

    /** {@inheritDoc} */
    public boolean isAlwaysOnTop() {
        return this.alwaysOnTop;
    }

    /** {@inheritDoc} */
    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public boolean isPickEnabled() {
        return this.pickEnabled;
    }

    /** {@inheritDoc} */
    public void setPickEnabled(boolean enable) {
        this.pickEnabled = enable;
    }

    @Override
    public double getDistanceFromEye() {
        return 0.0;
    }

    /**
     * Forwards the <code>MouseEvent</code> associated with the specified <code>event</code> to the balloon's internal
     * <code>WebView</code>. This does not consume the event, because the <code>{@link
     * gov.nasa.worldwind.event.InputHandler}</code> implements the policy for consuming or forwarding mouse clicked
     * events to other objects.
     *
     * @param event The event to handle.
     */
    public void selected(SelectEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleSelectEvent(event);
    }

    /**
     * Forwards the key typed event to the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>
     * and consumes the event. This consumes the event so the <code>{@link gov.nasa.worldwind.View}</code> doesn't
     * respond to it.
     *
     * @param event The event to forward.
     */
    public void keyTyped(KeyEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleKeyEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Forwards the key pressed event to the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>
     * and consumes the event. This consumes the event so the <code>{@link gov.nasa.worldwind.View}</code> doesn't
     * respond to it. The
     *
     * @param event The event to forward.
     */
    public void keyPressed(KeyEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleKeyEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Forwards the key released event to the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>
     * and consumes the event. This consumes the event so the <code>{@link gov.nasa.worldwind.View}</code> doesn't
     * respond to it.
     *
     * @param event The event to forward.
     */
    public void keyReleased(KeyEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleKeyEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Does nothing; BrowserBalloon handles mouse clicked events in <code>selected</code>.
     *
     * @param event The event to handle.
     */
    public void mouseClicked(MouseEvent event) {
   }

    /**
     * Does nothing; BrowserBalloon handles mouse pressed events in <code>selected</code>.
     *
     * @param event The event to handle.
     */
    public void mousePressed(MouseEvent event) {
    }

    /**
     * Does nothing; BrowserBalloon handles mouse released events in <code>selected</code>.
     *
     * @param event The event to handle.
     */
    public void mouseReleased(MouseEvent event) {
    }

    /**
     * Does nothing; BrowserBalloon does not handle mouse entered events.
     *
     * @param event The event to handle.
     */
    public void mouseEntered(MouseEvent event) {
    }

    /**
     * Does nothing; BrowserBalloon does not handle mouse exited events.
     *
     * @param event The event to handle.
     */
    public void mouseExited(MouseEvent event) {
    }

    /**
     * Does nothing; BrowserBalloon handles mouse dragged events in <code>selected</code>.
     *
     * @param event The event to handle.
     */
    public void mouseDragged(MouseEvent event) {
    }

    /**
     * Forwards the mouse moved event to the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>.
     * This does not consume the event, because the <code>{@link gov.nasa.worldwind.event.InputHandler}</code>
     * implements the policy for consuming or forwarding mouse moved events to other objects.
     * <p>
     * Unlike mouse clicked, mouse pressed, and mouse dragged events, mouse move events cannot be forwarded to the
     * WebView via SelectEvents in <code>selected</code>, because mouse movement events are not selection events.
     *
     * @param event The event to forward.
     */
    public void mouseMoved(MouseEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleMouseEvent(event);
    }

    /**
     * Forwards the mouse wheel event to the balloon's internal <code>{@link gov.nasa.worldwind.util.webview.WebView}</code>
     * and consumes the event. This consumes the event so the <code>{@link gov.nasa.worldwind.View}</code> doesn't
     * respond to it.
     * <p>
     * Unlike mouse clicked, mouse pressed, and mouse dragged events, mouse wheel events cannot be forwarded to the
     * WebView via SelectEvents in <code>selected</code>, because mouse wheel events are not selection events.
     *
     * @param event The event to forward.
     */
    public void mouseWheelMoved(MouseWheelEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }

        this.handleMouseEvent(event);
        event.consume(); // Consume the event so the View doesn't respond to it.
    }

    /**
     * Sends the specified <code>KeyEvent</code> to the balloon's internal <code>WebView</code>.
     * <p/>
     * This does nothing if the balloon's internal <code>WebView</code> is uninitialized.
     *
     * @param event the event to send.
     */
    protected void handleKeyEvent(KeyEvent event) {
        if (this.webView != null) {
 
           int keyCode = event.getKeyCode();
                try {
                    this.dispose();
                    avesViewer.keyPressed(event);
                } catch (Exception e) {
                	System.out.println(e);
                }

        }
    }

    /**
     * Sends the specified <code>MouseEvent</code> to the balloon's internal <code>WebView</code>. The event's point is
     * converted from AWT coordinates to the WebView's local coordinate system.
     * <p/>
     * This does nothing if the balloon's internal <code>WebView</code> is uninitialized.
     *
     * @param event the event to send.
     */
    protected void handleMouseEvent(MouseEvent event) {
        if (this.webView == null) {
            return;
        }

        // Convert the mouse event's screen point to the WebView's local coordinate system. Note that we send the mouse
        // event to the WebView even when its screen point is outside the WebView's bounding rectangle. This gives the
        // WebView a chance to change its state or the cursor's state when the cursor it exits the WebView.
        // without the following line, the cursor does not turn into a little selection hand
        Point webViewPoint = this.convertToWebView(event.getSource(), event.getPoint());

        // Send a copy of the mouse event using the point in the WebView's local coordinate system. 
        if (event instanceof MouseWheelEvent) {
            this.webView.sendEvent(
                    new MouseWheelEvent((Component) event.getSource(), event.getID(), event.getWhen(), event.getModifiers(),
                    webViewPoint.x, webViewPoint.y, event.getClickCount(), event.isPopupTrigger(),
                    ((MouseWheelEvent) event).getScrollType(), ((MouseWheelEvent) event).getScrollAmount(),
                    ((MouseWheelEvent) event).getWheelRotation()));
        } else {
            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), event.getID(), event.getWhen(), event.getModifiers(),
                    webViewPoint.x, webViewPoint.y, event.getClickCount(), event.isPopupTrigger(), event.getButton()));
        }
    }

    protected void handleSelectEvent(SelectEvent event) {
        if (this.webView == null) {
            return;
        }

        // Convert the mouse event's screen point to the WebView's local coordinate system. Note that we send the mouse
        // event to the WebView even when its screen point is outside the WebView's bounding rectangle. This gives the
        // WebView a chance to change its state or the cursor's state when the cursor it exits the WebView.
        Point pickPoint = event.getPickPoint();

        // The SelectEvent's pick point is null if its a drag end event. In this case, use pick point of the last
        // SelectEvent we received, which should be a drag event with a non-null pick point.
        if (pickPoint == null) {
            pickPoint = this.lastPickPoint;
        }

        // If the last SelectEvent's pick point is null and the current SelectEvent's pick point is null, then we cannot
        // send this event to the WebView.
        if (pickPoint == null) {
            return;
        }

        //without the following line, it does not work, probably because the source object coords are lost 
        Point webViewPoint = this.convertToWebView(event.getSource(), pickPoint);

        if (event.isLeftPress() || event.isRightPress()) {
            int modifiers = event.isLeftPress() ? MouseEvent.BUTTON1_DOWN_MASK : MouseEvent.BUTTON3_DOWN_MASK;
            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), modifiers, // when, modifiers.
                    webViewPoint.x, webViewPoint.y, 0, // x, y, clickCount.
                    event.isRightPress(), // isPopupTrigger.
                    event.isRightPress() ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1));
        } else if (event.isLeftClick() || event.isRightClick() || event.isLeftDoubleClick()) {
            int clickCount = event.isLeftDoubleClick() ? 2 : 1;
            int modifiers = (event.isLeftClick() || event.isLeftDoubleClick()) ? MouseEvent.BUTTON1_DOWN_MASK
                    : MouseEvent.BUTTON3_DOWN_MASK;

            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), MouseEvent.MOUSE_RELEASED,
                    System.currentTimeMillis(), 0, // when, modifiers.
                    webViewPoint.x, webViewPoint.y, clickCount, // x, y, clickCount.
                    false, // isPopupTrigger.
                    event.isRightClick() ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1));
            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(), modifiers, // when, modifiers.
                    webViewPoint.x, webViewPoint.y, clickCount, // x, y, clickCount
                    false, // isPopupTrigger.
                    event.isRightClick() ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1));
        } else if (event.isDrag()) {
            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), MouseEvent.MOUSE_DRAGGED,
                    System.currentTimeMillis(), MouseEvent.BUTTON1_DOWN_MASK, // when, modifiers.
                    webViewPoint.x, webViewPoint.y, 0, // x, y, clickCount.
                    false, // isPopupTrigger.
                    MouseEvent.BUTTON1));
        } else if (event.isDragEnd()) {
            this.webView.sendEvent(
                    new MouseEvent((Component) event.getSource(), MouseEvent.MOUSE_RELEASED,
                    System.currentTimeMillis(), 0, // when, modifiers.
                    webViewPoint.x, webViewPoint.y, 0, // x, y, clickCount.
                    false, // isPopupTrigger.
                    MouseEvent.BUTTON1));
        }

        this.lastPickPoint = event.getPickPoint();

        // Consume the SelectEvent now that it has been passed on to the WebView as a mouse event.
        event.consume();
    }

    /**
     * Converts the specified screen point from AWT coordinates to local WebView coordinates.
     *
     * @param point   The point to convert.
     * @param context the component who's coordinate system the point is in.
     *
     * @return A new <code>Point</code> in the WebView's local coordinate system.
     */
    protected Point convertToWebView(Object context, Point point) {
        int x = point.x;
        int y = point.y;

        // Translate AWT coordinates to OpenGL screen coordinates by moving the Y origin from the upper left corner to
        // the lower left corner and flipping the direction of the Y axis.
        if (context instanceof Component) {
            y = ((Component) context).getHeight() - point.y;
        }

        x -= this.webViewRect.x;
        y -= this.webViewRect.y;

        return new Point(x, y);
    }

    /**
     * Returns a <code>null</code> Cursor, indicating the default cursor should be used when the BrowserBalloon is
     * active. The Cursor is set by the <code>{@link gov.nasa.worldwind.util.webview.WebView}</code> in response to
     * mouse moved events.
     *
     * @return A <code>null</code> Cursor.
     */
    //needed for interface hotspot and for changing arrow into selection hand
    public Cursor getCursor() {
        return null;
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner() {
        return this.delegateOwner;
    }

    protected PickedObject createPickedObject(DrawContext dc, Color pickColor) {
        PickedObject po = new PickedObject(pickColor.getRGB(),
                this.getDelegateOwner() != null ? this.getDelegateOwner() : this);

        // Attach the balloon to the picked object's AVList under the key HOT_SPOT. The application can then find that
        // the balloon is a HotSpot by looking in the picked object's AVList. This is critical when the delegate owner
        // is not null because the balloon is no longer the picked object. This would otherwise prevent the application
        // from interacting with the balloon via the HotSpot interface.
        po.setValue(AVKey.HOT_SPOT, this);

        return po;
    }

    protected PickedObject createLinkPickedObject(DrawContext dc, Color pickColor, AVList linkParams) {
        PickedObject po = new PickedObject(pickColor.getRGB(), this);

        // Apply all of the link parameters to the picked object. This provides the application with the link's URL,
        // mime type, and target.
        po.setValues(linkParams);

        // Attach the balloon's context to the picked object to provide context for link clicked events. This supports
        // KML features that specify links with relative paths or fragments to the parent KML root.
        po.setValue(AVKey.CONTEXT, this.getValue(AVKey.CONTEXT));

        return po;
    }

    protected boolean isDrawInterior(DrawContext dc) {
        return true;      
    }

    protected boolean isDrawLinks(DrawContext dc) {
        return this.isDrawInterior(dc) && dc.isPickingMode();
    }

    protected void drawFrame(DrawContext dc) {
       dc.getGL().getGL2().glVertexPointer(2, GL2.GL_INT, 0, vbuf); 
       this.prepareToDrawInterior(dc);
       this.drawFrameInterior(dc);
    }

    protected void drawLinks(DrawContext dc) {
        this.drawWebViewLinks(dc);
    }

    protected void prepareToDrawInterior(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();

        if (!dc.isPickingMode()) {
            // Apply the balloon's background color and opacity if we're in normal rendering mode.
            Color color = new Color(255, 255, 255);        
            double opacity = 1.0d;        
            gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(),
                    (byte) (opacity < 1 ? (int) (opacity * 255 + 0.5) : 255));
        }

    }

    protected IntBuffer makeVtxRectangle(int x, int y, int width, int height) {
        if (width < 0) {
            String message = Logging.getMessage("Geom.WidthIsNegative");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (height < 0) {
            String message = Logging.getMessage("Geom.HeightIsNegative");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // The buffer contains eight coordinate pairs: two pairs for each corner.
        IntBuffer buffer = Buffers.newDirectIntBuffer(8);
        // Lower left corner.
        buffer.put(x);
        buffer.put(y);
        // Lower right corner.
        buffer.put(x + width);
        buffer.put(y);
        // Upper right corner.
        buffer.put(x + width);
        buffer.put(y + height);
        // Upper left corner.
        buffer.put(x);
        buffer.put(y + height);
        // Rewind and return.
        buffer.rewind();
        return buffer;
    }

    protected FloatBuffer makeTexRectangle(float x, float y, float width, float height) {
        if (width < 0) {
            String message = Logging.getMessage("Geom.WidthIsNegative");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (height < 0) {
            String message = Logging.getMessage("Geom.HeightIsNegative");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // The buffer contains eight coordinate pairs: two pairs for each corner.
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(8);
        // Lower left corner.
        buffer.put(x);
        buffer.put(y + height);
        // Lower right corner.
        buffer.put(x + width);
        buffer.put(y + height);
        // Upper right corner.
        buffer.put(x + width);
        buffer.put(y);
        // Upper left corner.
        buffer.put(x);
        buffer.put(y);
        // Rewind and return.
        buffer.rewind();
        return buffer;
    }

    protected void drawFrameInterior(DrawContext dc) {

        GL2 gl = dc.getGL().getGL2();

        boolean textureApplied = false;
        try {
            // Bind the WebView's texture representation as the current texture source if we're in normal rendering
            // mode. This also configures the texture matrix to transform texture coordinates from the balloon's vertex
            // coordinates to the WebView's screen rectangle. For this reason we use the balloon's vertex coordinates as
            // its texture coordinates.
            if (!dc.isPickingMode() && this.bindWebViewTexture(dc)) {
                // The WebView's texture is successfully bound. Enable GL texturing and set up the texture
                // environment to apply the texture in decal mode. Decal mode uses the texture color where the
                // texture's alpha is 1, and uses the balloon's background color where it's 0. The texture's
                // internal format must be RGBA to work correctly, and we assume that the WebView's texture format
                // is RGBA.
                gl.glVertexPointer(2, GL2.GL_INT, 0, vbuf);
                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
                gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, tbuf);//this.frameInfo.vertexBuffer);
                // Denote that the texture has been applied and that we need to restore the default texture state.


                textureApplied = true;
            }

            int rest = tbuf.remaining();

            // Draw the balloon's geometry as a triangle fan to display the interior. The balloon's vertices are 
            // represented by (x,y) pairs in screen coordinates. The number of vertices to draw is computed by dividing
            // the number of coordinates by 2, because each vertex has exactly two coordinates: x and y.
            gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, rest / 2);
        } finally {
            // Restore the previous texture state and client array state. We do this to avoid pushing and popping the
            // texture attribute bit, which is expensive. We disable textures, disable texture coordinate arrays, bind
            // texture id 0, set the default texture environment mode, and and set the texture coord pointer to 0.
            if (textureApplied) {
                gl.glDisable(GL2.GL_TEXTURE_2D);
                gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
                gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
                gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, null);
            }
        }

    }

    protected boolean bindWebViewTexture(DrawContext dc) {
        if (this.webView == null) {
            return false;
        }
        GL2 gl = dc.getGL().getGL2();

        texture = this.webView.getTextureRepresentation(dc);
        if (texture == null) {
            return false;
        }

        if (!texture.bind(dc)) {
            return false;
        }

        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glScalef(1f, 1f, 1f); // no scaling
        gl.glTranslatef(this.screenPoint.x, this.screenPoint.y, 0);
        // Restore the matrix mode.
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        return true;
    }

    protected void drawWebViewLinks(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();

        if (this.webView == null) {
            return;
        }

        Iterable<AVList> links = this.webView.getLinks();
        if (links == null) {
            return;
        }
        for (AVList linkParams : links) {
            // This should never happen, but we check anyway.
            if (linkParams == null) {
                continue;
            }

            // Ignore any links that have no bounds or no rectangles; they cannot be drawn.
            if (linkParams.getValue(AVKey.BOUNDS) == null || linkParams.getValue(AVKey.RECTANGLES) == null) {
                continue;
            }

            // Translate the bounds from WebView coordinates to World Window screen coordinates.
            Rectangle bounds = new Rectangle((Rectangle) linkParams.getValue(AVKey.BOUNDS));
            bounds.translate(this.webViewRect.x, this.webViewRect.y);

            Color pickColor = dc.getUniquePickColor();
            gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
            this.pickSupport.addPickableObject(this.createLinkPickedObject(dc, pickColor, linkParams));

            int x = this.webViewRect.x - this.screenRect.x;
            int y = this.webViewRect.y - this.screenRect.y;

            gl.glBegin(GL2.GL_QUADS);
            try {
                for (Rectangle rect : (Rectangle[]) linkParams.getValue(AVKey.RECTANGLES)) {
                    // This should never happen, but we check anyway.
                    if (rect == null) {
                        continue;
                    }

                    gl.glVertex2i(x + rect.x, y + rect.y);
                    gl.glVertex2i(x + rect.x + rect.width, y + rect.y);
                    gl.glVertex2i(x + rect.x + rect.width, y + rect.y + rect.height);
                    gl.glVertex2i(x + rect.x, y + rect.y + rect.height);
                }
            } finally {
                gl.glEnd();
            }
        }
    }

}
