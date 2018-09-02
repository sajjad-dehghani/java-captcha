/**
 * Created by sajjad.dehghani68@gmail.com
 * Date: 4/7/2018
 * Time: 2:51 PM
 */

package com.misc.digitalbanking.ui.util;

import com.misc.digitalbanking.ui.mainpage.Login;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * This is a captcha class, use it to generate a random string and then to create an image of it.
 */

@Named
@RequestScope
public class Captcha {

    @Autowired
    Login login;

    private StreamedContent captchaImage;

    public StreamedContent getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(StreamedContent captchaImage) {
        this.captchaImage = captchaImage;
    }

    @PostConstruct
    public void init() throws IOException {
        String captchaText = Captcha.generateText().substring(0, 6);
        login.setCaptchaOriginalValue(captchaText);
        captchaImage = new DefaultStreamedContent(new ByteArrayInputStream(generateImage(captchaText)));
    }

    /**
     * Generates a random alpha-numeric string of eight characters.
     *
     * @return random alpha-numeric string of eight characters.
     */
    public static String generateText() {
        return new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();
    }

    /**
     * Generates a PNG image of text 180 pixels wide, 50 pixels high with white background.
     *
     * @param text expects string size eight (8) characters.
     * @return byte array that is a PNG image generated with text displayed.
     */
    public static byte[] generateImage(String text) {

        int w = 170, h = 50;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Serif", Font.PLAIN, 26));
        g.setColor(Color.blue);
        int start = 10;
        byte[] bytes = text.getBytes();

        Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            // set random digit colors
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(new String(new byte[]{bytes[i]}), start + (i * 25), (int) (Math.random() * 20 + 25));
        }
        // to disable draw circles on image comment these lines
        g.setColor(Color.black);
        for (int i = 0; i < 6; i++) {
            g.drawOval((int) (Math.random() * 160), (int) (Math.random() * 10), 30, 30);
        }
        g.dispose();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bout.toByteArray();
    }

    /**
     * Generate a captcha image and set on text value on session as sessionKeyName.
     *
     * @return bytes Image
     * @throws IOException
     */
    public byte[] getCaptcha() throws IOException {

        String captchaText = Captcha.generateText().substring(0, 6);

        return Captcha.generateImage(captchaText);
    }
}
