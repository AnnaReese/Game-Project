import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * To use, 
 *    Image image = new Image("filename.jpg");
 * To change the image,
 *    image.setImage("filename.jpg");
 */

public class Image {

    public final String IMAGE_DIRECTORY = "./";
    private JLabel lbl;

    /** 
     * show the image in the given file
     */
    public Image()
    {
        lbl = null;
    }

    private void loadImage(String file)
    {
        try {
            if (file.indexOf(IMAGE_DIRECTORY) != 0)
                file = IMAGE_DIRECTORY + file;
            BufferedImage img=ImageIO.read(new File(file));
            ImageIcon icon=new ImageIcon(img);
            JFrame frame=new JFrame();
            frame.setLayout(new FlowLayout());
            frame.setSize(500,500);
            lbl=new JLabel();
            lbl.setIcon(icon);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * changes the image to a new one
     */
    public void setImage(String file) {
        if (file.indexOf(IMAGE_DIRECTORY) != 0)
            file = IMAGE_DIRECTORY + file;
        if (lbl == null) {
            loadImage(file);
        } else {
            try {
                BufferedImage img=ImageIO.read(new File(file));
                ImageIcon icon=new ImageIcon(img);
                lbl.setIcon(icon);
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }
}