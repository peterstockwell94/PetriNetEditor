import java.awt.*;
import javax.swing.JComponent;

public class TransitionIcon extends JComponent implements TransitionListener {
    private static final Color        BLUE = new Color(102, 217, 239);
    private static final Color   DARK_BLUE = new Color( 32, 149, 210);
    private static final Color      ORANGE = new Color(253, 151,  31);
    private static final Color DARK_ORANGE = new Color(227, 114,  52);
    private static final Color       GREEN = new Color(166, 226,  46);
    private static final Color        PINK = new Color(249,  38, 114);
    private static final Color      PURPLE = new Color(174, 129, 255);
    private static final Color       BLACK = new Color( 38,  41,  44);

    private static final int W = 100;
    private static final int H = 12;

    private Transition t;
    private Boolean selected;
    private Boolean addingArc;

    public TransitionIcon(Transition transition) {
        t = transition;
        selected = false;
        addingArc = false;
        setBounds(t.getX() - W/2, t.getY() - H/2, W, H + 16);
        setVisible(true);
        repaint();
    }

    public Transition getTransition() {
        return t;
    }

    public void transitionHasChanged() {
        setBounds(t.getX() - W/2, t.getY() - H/2, W, H + 16);
        revalidate();
        repaint();
    }

    public int xPos() {
        return t.getX();
    }

    public int yPos() {
        return t.getY();
    }

    public int edgeX(int x, int y) {
        int dx = x - xPos();
        int dy = y - yPos();
        int dd = Math.max(Math.abs(dx/(W/2)), Math.abs(dy/(H/2)));
        if (dd == 0) dd++;
        return dx/dd;
    }

    public int edgeY(int x, int y) {
        int dx = x - xPos();
        int dy = y - yPos();
        int dd = Math.max(Math.abs(dx/(W/2)), Math.abs(dy/(H/2)));
        if (dd == 0) dd++;
        return dy/dd;
    }

    public Boolean isSelected() {
        return selected;
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
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw Transition rectangle
        if      (selected)  g.setColor(PURPLE);
        else if (addingArc) g.setColor(PINK);
        else                g.setColor(GREEN);
        g.setStroke(new BasicStroke(2));
        g.fillRect(0, 0, W, H);
        g.setStroke(new BasicStroke(1));

        // Draw Transition text
        int nameWidth = g.getFontMetrics().stringWidth(t.getName());
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g.drawString(t.getName(), W/2 - nameWidth/2, H + 12);
    }

}
