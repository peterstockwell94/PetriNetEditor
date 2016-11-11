import java.awt.*;
import javax.swing.JComponent;

public class PlaceIcon extends JComponent implements PlaceListener {
    private static final Color        BLUE = new Color(102, 217, 239);
    private static final Color   DARK_BLUE = new Color( 32, 149, 210);
    private static final Color      ORANGE = new Color(253, 151,  31);
    private static final Color DARK_ORANGE = new Color(227, 114,  52);
    private static final Color       GREEN = new Color(166, 226,  46);
    private static final Color        PINK = new Color(249,  38, 114);
    private static final Color      PURPLE = new Color(174, 129, 255);
    private static final Color       BLACK = new Color( 38,  41,  44);

    private static final int R = 32;

    private Place p;
    private Boolean selected;
    private Boolean addingArc;

    public PlaceIcon(Place place) {
        p = place;
        selected = false;
        addingArc = false;
        setBounds(p.getX() - R, p.getY() - (R + 10), 2*R, 2*R + 10);
        setVisible(true);
        repaint();
    }

    public Place getPlace() {
        return p;
    }

    public void placeHasChanged() {
        setBounds(p.getX() - R, p.getY() - (R + 10), 2*R, 2*R + 10);
        revalidate();
        repaint();
    }

    public int xPos() {
        return p.getX();
    }

    public int yPos() {
        return p.getY();
    }

    public int edgeX(int x, int y) {
        int dx = x - xPos();
        int dy = y - yPos();
        int dd = (int) Math.sqrt(dx*dx + dy*dy);
        if (dd == 0) dd++;
        return (dx*R)/dd;
    }
    public int edgeY(int x, int y) {
        int dx = x - xPos();
        int dy = y - yPos();
        int dd = (int) Math.sqrt(dx*dx + dy*dy);
        if (dd == 0) dd++;
        return (dy*R)/dd;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean b) {
        selected = b;
        revalidate();
        repaint();
    }

    public void select() {
        selected = true;
        revalidate();
        repaint();
    }

    public void deselect() {
        selected = false;
        revalidate();
        repaint();
    }

    public void toggle() {
        selected = !selected;
        revalidate();
        repaint();
    }

    public void addArc() {
        addingArc = true;
        revalidate();
        repaint();
    }

    public void stopAddingArc() {
        addingArc = false;
        revalidate();
        repaint();
    }

    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw Place circle
        if (selected)       g.setColor(PURPLE);
        else if (addingArc) g.setColor(PINK);
        else                g.setColor(ORANGE);

        g.setStroke(new BasicStroke(2));
        if (selected || addingArc) g.fillArc(0, 10, 2*R - 2, 2*R - 2, 0, 360);
        else                       g.drawArc(0, 10, 2*R - 2, 2*R - 2, 0, 360);
        g.setStroke(new BasicStroke(1));

        // Text font
        int nameWidth = g.getFontMetrics().stringWidth(p.getName());
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));

        // Draw Initial Arc weights
        if (p.getInitialArcWeight() > 0) {
            if (selected)       g.setColor(PURPLE);
            else if (addingArc) g.setColor(PINK);
            else                g.setColor(ORANGE);
            g.drawString(Integer.toString(p.getInitialArcWeight()), 10, 8);
            g.drawLine(0, 0, 3, 9);
            g.drawLine(3, 9, 6, 5);
            g.drawLine(6, 5, 9, 18);
            g.drawLine(11, 12, 9, 18);
            g.drawLine(4, 16, 9, 18);
            // g.drawLine(4, 16, 11, 12);

        }

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        if (selected || addingArc) g.setColor(BLACK);
        else                       g.setColor(ORANGE);

        if (p.getMarking() > 0) {
            // Draw Place text
            g.drawString(p.getName(), R - nameWidth/2, R - R/8);

            // Draw Marking circle
            int markingWidth = g.getFontMetrics().stringWidth(Integer.toString(p.getMarking()));
            g.fillArc(R - (10 + markingWidth)/2, R + R/8, 10 + markingWidth, 10 + markingWidth, 0, 360);

            // Draw Marking text
            if (selected)       g.setColor(PURPLE);
            else if (addingArc) g.setColor(PINK);
            else                g.setColor(BLACK);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.drawString(Integer.toString(p.getMarking()), R - markingWidth/2, R + R/2);
        } else {
            // Draw Place text
            g.drawString(p.getName(), R - nameWidth/2, R + 6);
        }
    }

}
