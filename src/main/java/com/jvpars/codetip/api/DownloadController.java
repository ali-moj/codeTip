package com.jvpars.codetip.api;

import com.jvpars.codetip.dto.requests.FileRequest;
import com.jvpars.codetip.utils.DocumentService;
import com.jvpars.codetip.utils.FolderPath;
import com.jvpars.codetip.utils.GenericResponseGenerator;
import com.jvpars.codetip.utils.ImageResize;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.swing.FilePane;

@RestController
@RequestMapping(value = "/dl")
public class DownloadController {

    private final Logger log = LoggerFactory.getLogger(DownloadController.class);
    private DocumentService documentService;

    public DownloadController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @GetMapping(value = "/image/{folder}/{file}")
    public ResponseEntity<byte[]> getImage(@PathVariable String folder,
                                           @PathVariable String file,
                                           @RequestParam(value = "ratio", required = false) Integer ratio) {

        // log.info("avatar " + ratio);

        byte[] media = null;
        if (ratio != null) {
            media = documentService.Resize(folder, file, ratio);
        } else {
            media = documentService.readFile(folder, file);
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }


    @GetMapping(value = "/file/{folder}/{file}")
    public ResponseEntity<byte[]> getFile(@PathVariable String folder,
                                          @PathVariable String file) {
        byte[] media = documentService.readFile(folder, file);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/upload/{folderName}")
    public ResponseEntity saveDocument(@RequestBody FileRequest file, @PathVariable String folderName) {
        byte[] data = documentService.Base64ToByte(file.data);
        String url = documentService.saveFile(folderName, file.name, data);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping(value = "/chat/{folder}/{file}")
    public ResponseEntity getChatFile(@PathVariable String folder, @PathVariable String file, @RequestParam(value = "ratio", required = false) Integer ratio) {
        byte[] media = null;
        if (ratio != null) {
            media = documentService.ResizeChat(folder, file, ratio);
        } else {
            media = documentService.readChatFile(folder, file);
        }

        if (media == null)
            return new ResponseEntity("", HttpStatus.NOT_FOUND);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        if (folder.equals("image"))
            headers.setContentType(MediaType.IMAGE_JPEG);
        else
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }


    @PostMapping(value = "/multipart/{folder}/{extension}", consumes = {"multipart/form-data"})
    public ResponseEntity multipartUpload(@PathVariable String extension,
                                          @PathVariable String folder,
                                          @RequestParam("file") MultipartFile file) {
        log.info("multipartUpload call");
        try {
            String folderName = FolderPath.CHAT_FILE;
            String urlFolder = "file";
            if (folder.equals("1")) {
                folderName = FolderPath.CHAT_FILE;
                urlFolder = "file";
            } else if (folder.equals("2")) {
                folderName = FolderPath.CHAT_VIDEO;
                urlFolder = "video";
            } else if (folder.equals("3")) {
                folderName = FolderPath.CHAT_PICTURE;
                urlFolder = "image";
            }else if (folder.equals("4")) {
                folderName = FolderPath.CHAT_SOUND;
                urlFolder = "sound";
            }

            String url = documentService.SaveChatFile(folderName, urlFolder, extension, file.getBytes());
            return GenericResponseGenerator.success(url);
        } catch (Exception ex) {
            return GenericResponseGenerator.error(ex.getMessage());
        }
    }
}
