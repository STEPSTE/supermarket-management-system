package com.boutique.service;

import com.boutique.dto.PageResponse;
import com.boutique.dto.request.*;
import com.boutique.exception.BusinessException;
import com.boutique.exception.ResourceNotFoundException;
import com.boutique.model.*;
import com.boutique.repository.CategoryRepository;
import com.boutique.repository.ProductRepository;
import com.boutique.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EmailService emailService;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, 
                          CategoryRepository categoryRepository, EmailService emailService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.emailService = emailService;
    }

    public Product createProduct(CreateProductRequest request) {
        User creator = userRepository.findById(request.createdById())
                .orElseThrow(() -> new ResourceNotFoundException("Créateur non trouvé: " + request.createdById()));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setStatus(request.status() != null ? request.status() : ProductStatus.DISPONIBLE);
        product.setCreatedBy(creator);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée: " + request.categoryId()));
            product.setCategory(category);
        }

        if (request.photos() != null) {
            product.setPhotos(request.photos().stream()
                .map(p -> new Photo(p.url(), p.isMain() != null ? p.isMain() : false))
                .collect(Collectors.toList()));
        }

        if (request.variants() != null) {
            product.setVariants(request.variants().stream()
                .map(v -> {
                    Variant var = new Variant(v.type(), v.value());
                    var.setStock(v.stock());
                    var.setPrice(v.price());
                    var.setAvailable(v.available());
                    return var;
                }).collect(Collectors.toList()));
        }

        return productRepository.save(product);
    }

    public PageResponse<Product> findAll(int page, int size) {
        Page<Product> result = productRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(), 
                                  result.getTotalElements(), result.getTotalPages());
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé: " + id));
    }

    public List<Product> search(ProductSearchRequest request) {
        return productRepository.searchProducts(
            request.name(), request.categoryId(), request.minPrice(), 
            request.maxPrice(), request.status()
        );
    }

    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.stockQuantity() != null) product.setStockQuantity(request.stockQuantity());
        if (request.status() != null) product.setStatus(request.status());
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
            product.setCategory(category);
        }
        updateStatusBasedOnStock(product);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé: " + id);
        }
        productRepository.deleteById(id);
    }

    public Product incrementStock(Long id, int quantity) {
        Product product = findById(id);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        updateStatusBasedOnStock(product);
        return productRepository.save(product);
    }

    public Product decrementStock(Long id, int quantity) {
        Product product = findById(id);
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Stock insuffisant. Disponible: " + product.getStockQuantity());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        updateStatusBasedOnStock(product);
        return productRepository.save(product);
    }

    public Product setMainPhoto(Long productId, String url) {
        Product product = findById(productId);
        for (Photo p : product.getPhotos()) {
            p.setIsMain(p.getUrl().equals(url));
        }
        return productRepository.save(product);
    }

    public Product addComment(Long productId, CommentDto request) {
        Product product = findById(productId);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Comment comment = new Comment(user, request.content(), request.rating());
        product.getComments().add(comment);
        return productRepository.save(product);
    }

    private void updateStatusBasedOnStock(Product product) {
        if (product.getStockQuantity() <= 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
            emailService.sendStockAlert(product);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK || product.getStatus() == ProductStatus.RUPTURE) {
            product.setStatus(ProductStatus.DISPONIBLE);
        }
    }
}