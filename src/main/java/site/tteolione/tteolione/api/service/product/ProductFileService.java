package site.tteolione.tteolione.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.client.s3.S3ImageService;
import site.tteolione.tteolione.domain.file.File;
import site.tteolione.tteolione.domain.file.FileRepository;
import site.tteolione.tteolione.domain.file.constants.EPhotoType;
import site.tteolione.tteolione.domain.product.Product;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFileService {

    private final FileRepository fileRepository;
    private final S3ImageService s3ImageService;

    public List<File> saveImages(List<MultipartFile> files, Product product, EPhotoType type) {
        return files.stream()
                .map(file -> saveImage(file, product, type))
                .collect(Collectors.toList());
    }

    public File saveImage(MultipartFile receipt, Product product, EPhotoType type) {
        String uploadUrl = s3ImageService.upload(receipt);
        return fileRepository.save(File.create(uploadUrl, product, type));
    }
}
