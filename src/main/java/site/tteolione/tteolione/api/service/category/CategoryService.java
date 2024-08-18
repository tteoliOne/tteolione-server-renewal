package site.tteolione.tteolione.api.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.domain.category.Category;
import site.tteolione.tteolione.domain.category.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new GeneralException(Code.NOT_FOUND_CATEGORY));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
