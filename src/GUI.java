import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GUI {
    static JPanel paneLeft;
    static JPanel paneRight;
    static JSlider sliderX;
    static JSlider sliderY;
    static JSlider sliderZ;
    public static void main(String[] args) {
        JFrame frame = new JFrame("My GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 800);
        frame.setLayout(null);
        final Vertex camera = new Vertex(0, 0, 500);
        final double distance = 1000;
        paneLeft = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                Coords coords = new Coords(new Vertex(0, 0, 0), new Vertex(150, 0, 0), new Vertex(0, 150, 0), new Vertex(0, 0, 150));
                ArrayList<Square> cube = new ArrayList<>();

                cube.add(new Square(new Vertex(100, 100, 100), new Vertex(-100, 100, 100), new Vertex(-100, -100, 100), new Vertex(100, -100, 100)));
                cube.add(new Square(new Vertex(100, 100, 100), new Vertex(-100, 100, 100), new Vertex(-100, 100, -100), new Vertex(100, 100, -100)));
                cube.add(new Square(new Vertex(100, 100, -100), new Vertex(-100, 100, -100), new Vertex(-100, -100, -100), new Vertex(100, -100, -100)));
                cube.add(new Square(new Vertex(100, -100, -100), new Vertex(-100, -100, -100), new Vertex(-100, -100, 100), new Vertex(100, -100, 100)));
                cube.add(new Square(new Vertex(100, 100, 100), new Vertex(100, 100, -100), new Vertex(100, -100, -100), new Vertex(100, -100, 100)));
                cube.add(new Square(new Vertex(-100, 100, 100), new Vertex(-100, 100, -100), new Vertex(-100, -100, -100), new Vertex(-100, -100, 100)));

                double X = Math.toRadians(sliderX.getValue());
                double Y = Math.toRadians(sliderY.getValue());
                double Z = Math.toRadians(sliderZ.getValue());
                System.out.println("X = " + X);
                System.out.println("Y = " + Y);
                System.out.println("Z = " + Z);


                Matrix3 XTransform = new Matrix3(new double[] {
                        1, 0, 0,
                        0, Math.sin(X), Math.cos(X),
                        0, Math.cos(X), -Math.sin(X)
                });
                Matrix3 YTransform = new Matrix3(new double[] {
                        Math.sin(Y), 0, Math.cos(Y),
                        0, 1, 0,
                        Math.cos(Y), 0, -Math.sin(Y)
                });
                Matrix3 ZTransform = new Matrix3(new double[] {
                        Math.sin(Z), -Math.cos(Z), 0,
                        Math.cos(Z), Math.sin(Z), 0,
                        0, 0, 1
                });

                g2.translate(getWidth()/2, getHeight()/2);
                g2.setColor(Color.WHITE);

                Matrix3 transform = XTransform.multiply(YTransform.multiply(ZTransform));

                Vertex coord_center = transform.transform(coords.center);
                Vertex coord_x = transform.transform(coords.x);
                Vertex coord_y = transform.transform(coords.y);
                Vertex coord_z = transform.transform(coords.z);

                coord_center = coord_center.viewCoordinateTrans(camera);
                coord_x = coord_x.viewCoordinateTrans(camera);
                coord_y = coord_y.viewCoordinateTrans(camera);
                coord_z = coord_z.viewCoordinateTrans(camera);

                Point2D cc = coord_center.screenTransition(distance);
                Point2D cx = coord_x.screenTransition(distance);
                Point2D cy = coord_y.screenTransition(distance);
                Point2D cz = coord_z.screenTransition(distance);

                Path2D cpath = new Path2D.Double();
                cpath.moveTo(cc.getX(), cc.getY());

                g2.setColor(Color.red);
                cpath.lineTo(cx.getX(), cx.getY());
                cpath.closePath();
                g2.draw(cpath);

                cpath = new Path2D.Double();
                cpath.moveTo(cc.getX(), cc.getY());
                g2.setColor(Color.green);
                cpath.lineTo(cy.getX(), cy.getY());
                cpath.closePath();
                g2.draw(cpath);

                cpath = new Path2D.Double();
                cpath.moveTo(cc.getX(), cc.getY());
                g2.setColor(Color.blue);
                cpath.lineTo(cz.getX(), cz.getY());
                cpath.closePath();
                g2.draw(cpath);

                g2.setColor(Color.white);

                for (Square s : cube) {
                    Vertex v1 = transform.transform(s.v1);
                    Vertex v2 = transform.transform(s.v2);
                    Vertex v3 = transform.transform(s.v3);
                    Vertex v4 = transform.transform(s.v4);

                    v1 = v1.viewCoordinateTrans(camera);
                    v2 = v2.viewCoordinateTrans(camera);
                    v3 = v3.viewCoordinateTrans(camera);
                    v4 = v4.viewCoordinateTrans(camera);

                    Square sTemp = new Square(v1, v2, v3, v4);

                    Point2D vs1 = v1.screenTransition(distance);
                    Point2D vs2 = v2.screenTransition(distance);
                    Point2D vs3 = v3.screenTransition(distance);
                    Point2D vs4 = v4.screenTransition(distance);

                    if (sTemp.isVisible(camera)){

                        Path2D path = new Path2D.Double();
                        path.moveTo(vs1.getX(), vs1.getY());
                        path.lineTo(vs2.getX(), vs2.getY());
                        path.lineTo(vs3.getX(), vs3.getY());
                        path.lineTo(vs4.getX(), vs4.getY());
                        path.closePath();

                        g2.draw(path);
                    }
                }
            }
        };
        paneLeft.setBounds(0, 0, frame.getHeight(), frame.getHeight());
        paneRight = new JPanel(new GridBagLayout());
        paneRight.setBounds(frame.getHeight(), 0, frame.getWidth() - frame.getHeight(), frame.getHeight());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        sliderX = new JSlider(-180, 180, 0);
        sliderX.setSnapToTicks(true);
        sliderX.setMajorTickSpacing(30);
        sliderX.setMinorTickSpacing(10);
        sliderX.setPaintTicks(true);
        sliderX.setPaintLabels(true);
        constraints.gridy = 1;
        constraints.weightx = .95;
        constraints.gridx = 0;
        paneRight.add(sliderX, constraints);
        constraints.weightx = .05;
        constraints.gridx = 1;
        paneRight.add(new JLabel("X slider"), constraints);
        sliderY = new JSlider(-180, 180, 0);
        sliderY.setSnapToTicks(true);
        sliderY.setMajorTickSpacing(30);
        sliderY.setMinorTickSpacing(10);
        sliderY.setPaintTicks(true);
        sliderY.setPaintLabels(true);
        constraints.gridy = 2;
        constraints.weightx = .95;
        constraints.gridx = 0;
        paneRight.add(sliderY, constraints);
        constraints.weightx = .05;
        constraints.gridx = 1;
        paneRight.add(new JLabel("Y slider"), constraints);
        sliderZ = new JSlider(-180, 180, 0);
        sliderZ.setSnapToTicks(true);
        sliderZ.setMajorTickSpacing(30);
        sliderZ.setMinorTickSpacing(10);
        sliderZ.setPaintTicks(true);
        sliderZ.setPaintLabels(true);
        constraints.gridy = 3;
        constraints.weightx = .95;
        constraints.gridx = 0;
        paneRight.add(sliderZ, constraints);
        constraints.weightx = .05;
        constraints.gridx = 1;
        paneRight.add(new JLabel("Z slider"), constraints);
        frame.add(paneLeft);
        frame.add(paneRight);
        sliderX.addChangeListener(e -> paneLeft.repaint());
        sliderY.addChangeListener(e -> paneLeft.repaint());
        sliderZ.addChangeListener(e -> paneLeft.repaint());

        frame.setVisible(true);

        frame.setVisible(true);
    }
}