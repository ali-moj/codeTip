package com.jvpars.codetip.utils;

import lombok.Synchronized;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageResize {
    @Synchronized
    public static byte[] Resize(String filePath, int maxSize) {
        BufferedImage image = null;
        byte[] imageInByte = null;
        String resizeImageSrc = null;
        byte[] imageByte = null;
        try {
            imageByte = getFileByte(filePath);
            if (maxSize == 0)
                return imageByte;

            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            int width = image.getWidth();
            int height = image.getHeight();

            int[] ratio = ImageResize.getImageSize(width, height, maxSize);
            int ratioWidth = ratio[0];
            int ratioHeight = ratio[1];

            BufferedImage res = Thumbnails.of(image)
                    .size(ratioWidth, ratioHeight)
                    .outputFormat("jpg")
                    .asBufferedImage();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(res, "jpg", stream);
            stream.flush();
            imageInByte = stream.toByteArray();
            return imageInByte;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[] getImageSize(int width, int height, int maxSize) {
        int[] result = new int[2];
        if (height <= maxSize || width <= maxSize) {
            result[0] = width;
            result[1] = height;
            return result;
        }
        if (height > width) {
            double ratio = (double) maxSize / (double) width;
            result[0] = (int) ((double) width * ratio);
            result[1] = (int) ((double) height * ratio);
            return result;
        } else {
            double ratio = (double) maxSize / (double) height;
            result[0] = (int) ((double) width * ratio);
            result[1] = (int) ((double) height * ratio);
            return result;
        }

    }

    public static byte[] getFile(String filePath){

        byte[] imageByte = null;
        try {
            imageByte = getFileByte(filePath);
            return  imageByte;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getFileByte(String filePath) throws IOException {
        String filePathToGraphsDir = FindOS.getFolderPath();
        String path = filePathToGraphsDir + File.separator +filePath;
        byte[] imageByte;
        File file = new File(path);
        //MyArgUtils.print(path);
        if (!file.exists()) {

            String fName = filePathToGraphsDir + File.separator + "blank-image.jpg";
            file = new File(fName);
        }
        imageByte = FileUtils.readFileToByteArray(file);
        return imageByte;
    }
}
