package com.gmail.tomasatrat.backend.microservices.carosuel.services;

import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Barcode;
import com.gmail.tomasatrat.backend.data.entity.CarouselImages;
import com.gmail.tomasatrat.backend.repositories.BarcodeRepository;
import com.gmail.tomasatrat.backend.repositories.CarouselImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarouselService {

    private CarouselImagesRepository carouselImagesRepository;

    @Autowired
    public CarouselService(CarouselImagesRepository carouselImagesRepository) {
        this.carouselImagesRepository = carouselImagesRepository;
    }

    public List<CarouselImages> findAll() {
        return this.carouselImagesRepository.findAll();
    }

    public CarouselImages addItem(CarouselImages item) {
        return this.carouselImagesRepository.save(item);
    }

    public Optional<CarouselImages> findByID(Long id) {
        return this.carouselImagesRepository.findById(id);
    }

    public void delete(IDataEntity item) {
        this.carouselImagesRepository.delete((CarouselImages) item);
    }

    public void delete() {
        this.carouselImagesRepository.deleteAll();
    }
}
