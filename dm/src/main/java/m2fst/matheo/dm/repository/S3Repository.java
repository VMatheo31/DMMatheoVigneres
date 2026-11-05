package m2fst.matheo.dm.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@Repository
public class S3Repository {

    public static final String BUCKET_RESTAURANT = "restaurant";
    public static final String BUCKET_EVALUATION = "evaluation";

    private static final String S3_URL = "https://s3.bober.ovh";
    private static final String ACCESS_KEY_ID = "cqBlF80IVJrc1isWW6oK";
    private static final String SECRET_ACCESS_KEY = "KPJlY826Qi0d952w4GWmQIvA0zfvWi62hvIyiZlT";

    public String addFile(String bucket, String fileName, MultipartFile file) {
        try (S3Client client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.builder()
                                .accessKeyId(ACCESS_KEY_ID)
                                .secretAccessKey(SECRET_ACCESS_KEY)
                                .build()))
                .endpointOverride(URI.create(S3_URL))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();
            client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return S3_URL + "/" + bucket + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUploadFileUrl(String bucket, String key) {
        try (var s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                        .accessKeyId(ACCESS_KEY_ID)
                        .secretAccessKey(SECRET_ACCESS_KEY)
                        .build()))
                .endpointOverride(URI.create(S3_URL))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build()) {

            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            return s3Presigner.presignPutObject(request)
                    .url()
                    .toExternalForm();
        }
    }

    public String getFileUrl(String bucket, String filePath) {
        try (S3Presigner s3Presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.builder()
                                .accessKeyId(ACCESS_KEY_ID)
                                .secretAccessKey(SECRET_ACCESS_KEY)
                                .build()))
                .endpointOverride(URI.create(S3_URL))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build()) {

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(request)
                    .build();

            return s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toExternalForm();
        }
    }
}

