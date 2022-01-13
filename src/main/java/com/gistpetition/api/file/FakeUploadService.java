package com.gistpetition.api.file;


import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Profile("!prod")
public class FakeUploadService implements UploadService {
    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {

    }

    @Override
    public String getFileUrl(String fileName) {
        return null;
    }
}
