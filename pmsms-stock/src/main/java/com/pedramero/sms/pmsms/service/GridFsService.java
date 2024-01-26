package com.pedramero.sms.pmsms.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GridFsService {
    @Autowired
    GridFsTemplate gridFsTemplate;

    public String saveFile(Resource resource, String objectId, String objectName)
        throws IOException {
        var metaData = new BasicDBObject();
        metaData.put("referenceID", objectId);
        metaData.put("referenceName", objectName);
        metaData.put("uploadDate", LocalDateTime.now());
        return gridFsTemplate.store(resource.getInputStream(), resource.getFilename(), metaData)
            .toHexString();
    }

    public Resource getFile(String id){
        var gridFsFile = gridFsTemplate.findOne(query(where("_id").is(id)));
        assert gridFsFile !=null;
        return gridFsTemplate.getResource(gridFsFile);
    }

    public Optional<String> getFileIdByName(String fileName) {
        var gridFsFile = gridFsTemplate.findOne(query(where("filename").is(fileName)));
        if (gridFsFile != null) {
            return Optional.of(gridFsFile.getObjectId().toHexString());
        }
        return Optional.empty();
    }

    public PathResource getResource(BufferedImage image, MultipartFile multipartFile)
        throws IOException {
        var resizedImage = simpleResizeImage(image);
        var extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        var file = Files.createTempFile("", multipartFile.getOriginalFilename());
        ImageIO.write(resizedImage, extension, file.toFile());
        var resource = new PathResource(file);
        return resource;
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public BufferedImage simpleResizeImage(BufferedImage originalImage) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 96, 96);
    }


}
