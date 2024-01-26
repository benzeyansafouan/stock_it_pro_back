package com.pedramero.sms.pmsms.service;


import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

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
}
