import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EditorPanel extends JPanel implements PetriNetListener, MouseListener, MouseMotionListener {
    private static final Color        BLUE = new Color(102, 217, 239);
    private static final Color   DARK_BLUE = new Color( 32, 149, 210);
    private static final Color      ORANGE = new Color(253, 151,  31);
    private static final Color DARK_ORANGE = new Color(227, 114,  52);
    private static final Color       GREEN = new Color(166, 226,  46);
    private static final Color        PINK = new Color(249,  38, 114);
    private static final Color      PURPLE = new Color(174, 129, 255);
    private static final Color       BLACK = new Color( 38,  41,  44);

    private static final Boolean PLACE_TO_TRANSITION = false;
    private static final Boolean TRANSITION_TO_PLACE = true;

    private Map<Place, PlaceIcon> placeIcons;
    private Map<Transition, TransitionIcon> transitionIcons;
    private PetriNet pn;
    private int x0;
    private int y0;
    private Rectangle rect;
    private Line2D rubberband;
    private Boolean selecting;
    private Boolean movingSelected;
    private Boolean arcDirection;
    private Boolean addingArc;
    private PlaceIcon arcPlace;
    private TransitionIcon arcTransition;

    public String lastSelectedTransition;

    public EditorPanel(PetriNet p) {
        super(null);
        setBackground(BLACK);
        pn = p;
        placeIcons = new HashMap<Place, PlaceIcon>();
        transitionIcons = new HashMap<Transition, TransitionIcon>();
        rect = new Rectangle(0,0,0,0);
        rubberband = new Line2D.Float(0,0,0,0);
        selecting = false;
        movingSelected = false;
        addingArc = false;
        arcPlace = null;
        arcTransition = null;
        arcDirection = null;
        lastSelectedTransition = "";
        addMouseListener(this);
        addMouseMotionListener(this);
        petrinetHasChanged();
    }

    public void setPetriNet(PetriNet p) {
        pn = p;
        petrinetHasChanged();
    }

    public void addArc() {
        arcPlace = null;
        arcTransition = null;
        addingArc = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void stopAddingArc() {
        addingArc = false;
        rubberband = new Line2D.Float(0,0,0,0);
        if (arcPlace != null) arcPlace.stopAddingArc();
        if (arcTransition != null) arcTransition.stopAddingArc();
        arcPlace = null;
        arcTransition = null;
        setCursor(Cursor.getDefaultCursor());
    }

    public Set<PlaceIcon> selectedPlaces() {
        Set<PlaceIcon> places = new HashSet<PlaceIcon>();
        for (Map.Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
            PlaceIcon pi = entry.getValue();
            if (pi.isSelected()) places.add(pi);
        }
        return places;
    }

    public Set<TransitionIcon> selectedTransitions() {
        Set<TransitionIcon> transitions = new HashSet<TransitionIcon>();
        for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
            TransitionIcon ti = entry.getValue();
            if (ti.isSelected()) transitions.add(ti);
        }
        return transitions;
    }

    private void moveSelected(MouseEvent me) {
        for (PlaceIcon pi : selectedPlaces()) pi.getPlace().moveBy(me.getX() - x0, me.getY() - y0);
        for (TransitionIcon ti : selectedTransitions()) ti.getTransition().moveBy(me.getX() - x0, me.getY() - y0);
        x0 = me.getX();
        y0 = me.getY();
    }

    private void deselectAll() {
        for (PlaceIcon pi : selectedPlaces()) pi.deselect();
        for (TransitionIcon ti : selectedTransitions()) ti.deselect();
    }

    public void petrinetHasChanged() {
        removeAll();
        placeIcons = new HashMap<Place, PlaceIcon>();
        for (Place p : pn.getPlaces()) {
            PlaceIcon pi = new PlaceIcon(p);
            p.addListener(pi);
            placeIcons.put(p, pi);
            add(pi);
        }
        for (Transition t : pn.getTransitions()) {
            TransitionIcon ti = new TransitionIcon(t);
            t.addListener(ti);
            transitionIcons.put(t, ti);
            add(ti);
        }
        revalidate();
        repaint();
    }

    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(PINK);
        g.setStroke(new BasicStroke(2));
        if (addingArc && (arcPlace != null ^ arcTransition != null)) g.draw(rubberband);
        g.setStroke(new BasicStroke(1));

        g.setColor(PURPLE);
        if (selecting) g.draw(rect);

        for (Place p : pn.getPlaces()) {
            PlaceIcon pi = placeIcons.get(p);
            for (Transition t : p.postSet()) {
                TransitionIcon ti = transitionIcons.get(t);
                int x1 = pi.xPos();
                int x2 = ti.xPos();

                int y1 = pi.yPos();
                int y2 = ti.yPos();

                int dx = x2 - x1;
                int dy = y2 - y1;
                int d = (int) Math.sqrt(dx*dx + dy*dy);
                if (d == 0) d++;

                int xc = x1 + dx/2 - (32*dy)/d;
                int yc = y1 + dy/2 + (32*dx)/d;

                int px = pi.edgeX(xc, yc) + x1;
                int py = pi.edgeY(xc, yc) + y1;

                int tx = ti.edgeX(xc, yc) + x2;
                int ty = ti.edgeY(xc, yc) + y2;

                g.setColor(BLUE);
                g.setStroke(new BasicStroke(2));
                g.draw(new QuadCurve2D.Float(px, py, xc, yc, tx, ty));
                g.setStroke(new BasicStroke(1));
                g.drawString(Integer.toString(pn.getArcWeight(p, t)), (px+xc)/2, (py+yc)/2);

                int ax = xc - tx;
                int ay = yc - ty;
                int r = (int) Math.sqrt(ax*ax + ay*ay);
                if (r == 0) r++;

                int xh = tx + (8*ax)/r;
                int yh = ty + (8*ay)/r;

                int ap = (4*ay)/r;
                int aq = (4*ax)/r;

                int[] xpoints = {tx, xh-ap, xh+ap};
                int[] ypoints = {ty, yh+aq, yh-aq};

                g.fillPolygon(xpoints, ypoints, 3);
            }
        }

        for (Transition tr : pn.getTransitions()) {
            TransitionIcon ti = transitionIcons.get(tr);
            for (Place p : tr.postSet()) {
                PlaceIcon pi = placeIcons.get(p);
                int x1 = ti.xPos();
                int x2 = pi.xPos();

                int y1 = ti.yPos();
                int y2 = pi.yPos();

                int dx = x2 - x1;
                int dy = y2 - y1;
                int d = (int) Math.sqrt(dx*dx + dy*dy);
                if (d == 0) d++;

                int xc = x1 + dx/2 - (32*dy)/d;
                int yc = y1 + dy/2 + (32*dx)/d;

                int tx = ti.edgeX(xc, yc) + x1;
                int ty = ti.edgeY(xc, yc) + y1;

                int px = pi.edgeX(xc, yc) + x2;
                int py = pi.edgeY(xc, yc) + y2;

                g.setColor(DARK_BLUE);
                g.setStroke(new BasicStroke(2));
                g.draw(new QuadCurve2D.Float(tx, ty, xc, yc, px, py));
                g.setStroke(new BasicStroke(1));
                g.drawString(Integer.toString(pn.getArcWeight(tr, p)), (tx+xc)/2, (ty+yc)/2);

                int ax = xc - px;
                int ay = yc - py;
                int r = (int) Math.sqrt(ax*ax + ay*ay);
                if (r == 0) r++;

                int xh = px + (8*ax)/r;
                int yh = py + (8*ay)/r;

                int ap = (4*ay)/r;
                int aq = (4*ax)/r;

                int[] xpoints = {px, xh-ap, xh+ap};
                int[] ypoints = {py, yh+aq, yh-aq};
                g.fillPolygon(xpoints, ypoints, 3);
            }
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (!addingArc) {
            // Select a single component
            for (Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
                PlaceIcon pi = entry.getValue();
                if (pi.getBounds().contains(me.getPoint())) {
                    pi.toggle();
                } else {
                    pi.deselect();
                }
            }
            for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
                TransitionIcon ti = entry.getValue();
                if (ti.getBounds().contains(me.getPoint())) {
                    ti.toggle();
                    lastSelectedTransition = entry.getKey().getName();
                } else {
                    ti.deselect();
                }
            }
        }
    }

    public void mousePressed(MouseEvent me) {
        if (addingArc) {
            deselectAll();
            for (Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
                PlaceIcon pi = entry.getValue();
                if (pi.getBounds().contains(me.getPoint())) {
                    pi.addArc();
                    arcPlace = pi;
                    arcDirection = PLACE_TO_TRANSITION;
                    x0 = me.getX();
                    y0 = me.getY();
                }
            }
            for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
                TransitionIcon ti = entry.getValue();
                if (ti.getBounds().contains(me.getPoint())) {
                    ti.addArc();
                    arcTransition = ti;
                    arcDirection = TRANSITION_TO_PLACE;
                    x0 = me.getX();
                    y0 = me.getY();
                }
            }
        } else {
            selecting = true;
            for (Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
                PlaceIcon pi = entry.getValue();
                if (pi.getBounds().contains(me.getPoint())) selecting = false;
            }
            for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
                TransitionIcon ti = entry.getValue();
                if (ti.getBounds().contains(me.getPoint())) selecting = false;
            }
            if (!selecting) {
                for (PlaceIcon pi : selectedPlaces()) {
                    if (pi.getBounds().contains(me.getPoint())) {
                        movingSelected = true;
                    }
                }
                for (TransitionIcon ti : selectedTransitions()) {
                    if (ti.getBounds().contains(me.getPoint())) {
                        movingSelected = true;
                    }
                }
                if (!movingSelected) deselectAll();
            }

            x0 = me.getX();
            y0 = me.getY();
            rect = new Rectangle(x0,y0,0,0);
        }
    }

    public void mouseReleased(MouseEvent me) {
        if (addingArc) {
            if (arcPlace == null) {
                for (Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
                    PlaceIcon pi = entry.getValue();
                    if (pi.getBounds().contains(me.getPoint())) {
                        pi.addArc();
                        arcPlace = pi;
                    }
                }
            } else {
                for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
                    TransitionIcon ti = entry.getValue();
                    if (ti.getBounds().contains(me.getPoint())) {
                        ti.addArc();
                        arcTransition = ti;
                    }
                }
            }
            if (arcPlace != null && arcTransition != null) {
                String weight = (String) JOptionPane.showInputDialog(this, "Enter the desired weight of the Arc", "Add Arc", JOptionPane.PLAIN_MESSAGE);
                if (weight != null) {
                    try {
                        int w = Integer.parseInt(weight);
                        Place p = arcPlace.getPlace();
                        Transition t = arcTransition.getTransition();
                        if (arcDirection == PLACE_TO_TRANSITION) {
                            pn.addArc(p, t, w);
                            PetriNetEditor.log("Arc with weight " + w + " added from " + p.getName() + " to " + t.getName());
                        } else if (arcDirection == TRANSITION_TO_PLACE) {
                            pn.addArc(t, p, w);
                            PetriNetEditor.log("Arc with weight " + w + " added from " + t.getName() + " to " + p.getName());
                        }
                    } catch (IllegalArgumentException e) {
                        PetriNetEditor.log("Error Adding Arc: \"" + weight + "\" is not a valid integer");
                        mouseReleased(me);
                    }
                }
            }
            stopAddingArc();
        }
        movingSelected = false;
        rect = new Rectangle(0,0,0,0);
        repaint();
    }

    public void mouseEntered(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {}

    public void mouseDragged(MouseEvent me) {
        if (addingArc) {
            int x = me.getX();
            int y = me.getY();
            rubberband = new Line2D.Float(x0, y0, x, y);
        } else if (selecting) {
            // Draw rectangle to show selection
            int x = me.getX();
            int y = me.getY();
            if (x >= x0 && y >= y0) rect = new Rectangle(x0, y0,  x - x0,  y - y0);
            if (x >= x0 && y <  y0) rect = new Rectangle(x0,  y,  x - x0, y0 -  y);
            if (x <  x0 && y >= y0) rect = new Rectangle( x, y0, x0 -  x,  y - y0);
            if (x <  x0 && y <  y0) rect = new Rectangle( x,  y, x0 -  x, y0 -  y);

            // Select components inside rectangle
            for (Component c : getComponents()) {
                if (rect.intersects(c.getBounds())) {
                if (c instanceof PlaceIcon) {
                        ((PlaceIcon) c).select();
                    } else {
                        ((TransitionIcon) c).select();
                        lastSelectedTransition = ((TransitionIcon) c).getTransition().getName();
                    }
                } else {
                    if (c instanceof PlaceIcon) ((PlaceIcon) c).deselect();
                    else ((TransitionIcon) c).deselect();
                }
            }
        } else {
            // Move a single component that is unselected
            if (selectedPlaces().isEmpty() && selectedTransitions().isEmpty()) {
                for (Entry<Place, PlaceIcon> entry : placeIcons.entrySet()) {
                    PlaceIcon pi = entry.getValue();
                    if (pi.getBounds().contains(me.getPoint())) {
                        pi.getPlace().moveBy(me.getX() - x0, me.getY() - y0);
                        x0 = me.getX();
                        y0 = me.getY();
                    }
                }
                for (Entry<Transition, TransitionIcon> entry : transitionIcons.entrySet()) {
                    TransitionIcon ti = entry.getValue();
                    if (ti.getBounds().contains(me.getPoint())) {
                        ti.getTransition().moveBy(me.getX() - x0, me.getY() - y0);
                        x0 = me.getX();
                        y0 = me.getY();
                    }
                }
            } else {
                // Move selected components
                for (PlaceIcon pi : selectedPlaces()) {
                    if (pi.getBounds().contains(me.getPoint())) {
                        moveSelected(me);
                    }
                }
                for (TransitionIcon ti : selectedTransitions()) {
                    if (ti.getBounds().contains(me.getPoint())) {
                        moveSelected(me);
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    public void mouseMoved(MouseEvent me) {}

}
