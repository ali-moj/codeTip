package com.jvpars.codetip.utils;

import com.jvpars.codetip.domain.enumitem.MessageType;
import com.jvpars.codetip.dto.requests.FileRequest;
import lombok.Synchronized;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@Transactional(readOnly = true)
public class DocumentService {


    private final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Transactional(readOnly = false)
    public void createPrimaryFolder() {
        String mainPath = System.getProperty("user.home");
        mainPath = mainPath + File.separator + FolderPath.MAIN_PATH;
        FindOS.folderCreateIfNotExist(mainPath);
        String profileFolder = mainPath + File.separator + FolderPath.USER_PROFILE;
        FindOS.folderCreateIfNotExist(profileFolder);
        String articleFolder = mainPath + File.separator + FolderPath.ARTICLE_FILE;
        FindOS.folderCreateIfNotExist(articleFolder);
        String roomFolder = mainPath + File.separator + FolderPath.CHAT;
        FindOS.folderCreateIfNotExist(roomFolder);

        String chatFile = mainPath + File.separator + FolderPath.CHAT_FILE;
        FindOS.folderCreateIfNotExist(chatFile);
        String chatImage = mainPath + File.separator + FolderPath.CHAT_PICTURE;
        FindOS.folderCreateIfNotExist(chatImage);
        String chatVideo = mainPath + File.separator + FolderPath.CHAT_VIDEO;
        FindOS.folderCreateIfNotExist(chatVideo);
        String chatVoice = mainPath + File.separator + FolderPath.CHAT_VOICE;
        FindOS.folderCreateIfNotExist(chatVoice);
        String chatSound = mainPath + File.separator + FolderPath.CHAT_SOUND;
        FindOS.folderCreateIfNotExist(chatVoice);
    }

    @Transactional(readOnly = false)
    public void SaveFile(byte[] data, String filename) {
        try {
            String mainPath = FindOS.getFolderPath();
            FindOS.folderCreateIfNotExist(mainPath);
            Path filePath = Paths.get(mainPath + File.separator + filename);
            Files.write(filePath, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Transactional(readOnly = false)
    public String SaveFile(FileRequest file) {
        try {

            String imageBase64 = file.data;
            String partSeparator = ",";
            if (imageBase64.contains(partSeparator)) {
                String encodedImg = imageBase64.split(partSeparator)[1];
                byte[] encoded = Base64.getDecoder()
                        .decode(encodedImg.getBytes(StandardCharsets.UTF_8));


                String extension = FilenameUtils.getExtension(file.name);
                String downloadKey = MyArgUtils.randomAlphaNumeric(20) + "." + extension;
                String mainPath = FindOS.getFolderPath();
                FindOS.folderCreateIfNotExist(mainPath);
                Path filePath = Paths.get(mainPath + File.separator + downloadKey);
                Files.write(filePath, encoded);
                return "/dl/image/" + downloadKey;
            }
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }

    @Transactional(readOnly = false)
    public String SaveFile(FileRequest file, String folder) {
        try {

            String imageBase64 = file.data;
            String partSeparator = ",";
            if (imageBase64.contains(partSeparator)) {
                String encodedImg = imageBase64.split(partSeparator)[1];
                byte[] encoded = Base64.getDecoder()
                        .decode(encodedImg.getBytes(StandardCharsets.UTF_8));


                String extension = FilenameUtils.getExtension(file.name);
                String downloadKey = MyArgUtils.randomAlphaNumeric(20) + "." + extension;
                String mainPath = FindOS.getFolderPath();
                mainPath = mainPath + File.separator + folder;
                FindOS.folderCreateIfNotExist(mainPath);
                Path filePath = Paths.get(mainPath + File.separator + downloadKey);
                Files.write(filePath, encoded);
                return "/dl/file/" + folder + "/" + downloadKey;
            }
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }

    public String SaveChatFile(FileRequest file, MessageType type) {
        try {

            String base64 = new String(file.data.getBytes(), StandardCharsets.UTF_8);
            String partSeparator = ",";
            if (base64.contains(partSeparator))
                base64 = base64.split(partSeparator)[1];

           // log.info(base64);
            byte[] encoded = Base64Utils.decodeFromString(base64);

            String extension = FilenameUtils.getExtension(file.name);
            String downloadKey = MyArgUtils.randomAlphaNumeric(20) + "." + extension;
            String mainPath = FindOS.getFolderPath();
            String path = "";
            String dlUrl = "";
            switch (type) {
                case FILE:
                    path = mainPath + File.separator + FolderPath.CHAT_FILE + File.separator + downloadKey;
                    dlUrl = "/dl/chat/file/";
                    break;
                case VIDEO:
                    path = mainPath + File.separator + FolderPath.CHAT_VIDEO + File.separator + downloadKey;
                    dlUrl = "/dl/chat/video/";
                    break;
                case PICTURE:
                    path = mainPath + File.separator + FolderPath.CHAT_PICTURE + File.separator + downloadKey;
                    dlUrl = "/dl/chat/image/";
                    break;
                case VOICE:
                    path = mainPath + File.separator + FolderPath.CHAT_VOICE + File.separator + downloadKey;
                    dlUrl = "/dl/chat/voice/";
                    break;
            }
            Path filePath = Paths.get(path);
            Files.write(filePath, encoded);
            //log.info(dlUrl + downloadKey);
            return dlUrl + downloadKey;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String SaveChatFile(String folderName , String urlFolder , String extension , byte[] data) {
        try {


            String downloadKey = MyArgUtils.randomAlphaNumeric(20) + "." + extension;
            String mainPath = FindOS.getFolderPath();
            String path = mainPath +  File.separator + folderName + File.separator + downloadKey ;
            String dlUrl = "/dl/chat/"+urlFolder+"/" ;
            Path filePath = Paths.get(path);
            Files.write(filePath, data);
            return dlUrl + downloadKey;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    @Transactional(readOnly = false)
    public boolean createFolder(String folderName) {
        try {
            String path = System.getProperty("user.home");
            path = path + File.separator + FolderPath.MAIN_PATH;
            path = String.format("%s%s%s", path, File.separator, folderName);
            FindOS.folderCreateIfNotExist(path);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public byte[] readFile(String fileName) {
        try {
            String mainPath = String.format("%s%s%s", FindOS.getFolderPath(), File.separator, fileName);
            File file = new File(mainPath);
            return FileUtils.readFileToByteArray(file);
        } catch (Exception ex) {
            return null;
        }
    }

    public byte[] readFile(String folderName, String fileName) {
        try {
            File file = new File(createPath(folderName, fileName));
            if (!file.exists()) {
                file = new File(String.format("%s%s%s", FindOS.getFolderPath(), File.separator, FolderPath.NO_IMAGE));
            }
            return FileUtils.readFileToByteArray(file);
        } catch (Exception ex) {
            return null;
        }
    }

    public byte[] readChatFile(String folderName, String fileName) {
        try {
            File file = new File(createChatFilePath(folderName, fileName));
            if (!file.exists()) {
                return null;
            }
            return FileUtils.readFileToByteArray(file);
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(readOnly = false)
    public String saveFile(String folderName, String fileName, byte[] data) {
        try {
            Path filePath = Paths.get(createPath(folderName, fileName));
            //log.info(data.length + "");
            Files.write(filePath, data);
            return String.format("/dl/file/%s/%s", folderName, fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional(readOnly = false)
    public String saveImageFile(String folderName, String fileName, byte[] data) {
        try {
            String mainPath = String.format("%s%s%s", FindOS.getFolderPath(), File.separator, folderName);
            mainPath = String.format("%s%s%s", mainPath, File.separator, fileName);
            Path filePath = Paths.get(mainPath);
            Files.write(filePath, data);
            return String.format("/dl/image/%s/%s", folderName, fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] Base64ToByte(String base64) {

        String partSeparator = ",";
        if (base64.contains(partSeparator)) {
            base64 = base64.split(partSeparator)[1];
        }

        return Base64.getDecoder()
                .decode(base64.getBytes(StandardCharsets.UTF_8));

    }

    @Synchronized
    public byte[] Resize(String folder, String fileName, int ratio) {
        BufferedImage image = null;
        byte[] imageInByte = null;
        String resizeImageSrc = null;
        byte[] imageByte = null;
        try {

            String mainPath = System.getProperty("user.home");
            mainPath = mainPath + File.separator + FolderPath.MAIN_PATH;
            String filePath = String.format("%s%s%s%s%s", mainPath, File.separator, folder, File.separator, fileName);
            File file = new File(filePath);
            if (!file.exists()) {
                filePath = String.format("%s%s%s", mainPath, File.separator, FolderPath.NO_IMAGE);
                file = new File(filePath);
            }
            if (file.exists())
                imageByte = FileUtils.readFileToByteArray(file);

            if (ratio == 0)
                return imageByte;

            if (imageByte == null)
                return null;

            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            int width = image.getWidth();
            int height = image.getHeight();

            int[] imageSize = getImageSize(width, height, ratio);
            int ratioWidth = imageSize[0];
            int ratioHeight = imageSize[1];

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

    @Synchronized
    public byte[] ResizeChat(String folder, String fileName, int ratio) {
        BufferedImage image = null;
        byte[] imageInByte = null;
        String resizeImageSrc = null;
        byte[] imageByte = null;
        try {
            String mainPath = System.getProperty("user.home");
            mainPath = mainPath + File.separator + FolderPath.MAIN_PATH;
            String filePath = "";
            File file = new File(createChatFilePath(folder, fileName));
            if (!file.exists()) {
                filePath = String.format("%s%s%s", mainPath, File.separator, FolderPath.NO_IMAGE);
                file = new File(filePath);
            }
            if (file.exists())
                imageByte = FileUtils.readFileToByteArray(file);

            if (ratio == 0)
                return imageByte;

            if (imageByte == null)
                return null;

            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            int width = image.getWidth();
            int height = image.getHeight();

            int[] imageSize = getImageSize(width, height, ratio);
            int ratioWidth = imageSize[0];
            int ratioHeight = imageSize[1];

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

    private int[] getImageSize(int width, int height, int maxSize) {
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

    public Boolean deleteFile(String folderName, String fileName) {
        try {
            File file = new File(createPath(folderName, fileName));
            return file.delete();
        } catch (Exception ex) {
            return false;
        }
    }

    private String createPath(String folderName, String fileName) {
        String path = System.getProperty("user.home");
        path = path + File.separator + FolderPath.MAIN_PATH;
        path = String.format("%s%s%s", path, File.separator, folderName);
        path = String.format("%s%s%s", path, File.separator, fileName);
        //log.info(path);
        return path;
    }

    private String createChatFilePath(String folderName, String fileName) {
        String path = System.getProperty("user.home");
        path = path + File.separator + FolderPath.MAIN_PATH;
        path = String.format("%s%s%s", path, File.separator, FolderPath.CHAT);
        path = String.format("%s%s%s", path, File.separator, folderName);
        path = String.format("%s%s%s", path, File.separator, fileName);
        log.info(path);
        return path;
    }
}
