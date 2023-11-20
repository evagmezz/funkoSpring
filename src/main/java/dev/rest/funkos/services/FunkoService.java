package dev.rest.funkos.services;

import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FunkoService {

    Page<FunkoResponseDto> findAll(Optional<String> categoria, Optional<String> nombre, Optional<Double> minPrecio, Pageable pageable);

    FunkoResponseDto findById(Long id);

    FunkoResponseDto save(FunkoCreateDto funkoCreateDto);

    FunkoResponseDto update(FunkoUpdateDto funkoUpdateDto, Long id);

    void deleteById(Long id);

    FunkoResponseDto updateImage(Long id, MultipartFile image, Boolean withUrl);
}
