package com.example.gistcompetitioncnserver.file;


import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
