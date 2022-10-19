package com.gmail.tomasatrat.ui.views.carousel;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.gmail.tomasatrat.app.HasLogger;
import com.gmail.tomasatrat.backend.data.Role;
import com.gmail.tomasatrat.backend.data.entity.CarouselImages;
import com.gmail.tomasatrat.backend.data.entity.OrderDetail;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.microservices.carosuel.services.CarouselService;
import com.gmail.tomasatrat.ui.MainView;
import com.gmail.tomasatrat.ui.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.security.access.annotation.Secured;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gmail.tomasatrat.ui.utils.Constants.PAGE_CAROUSEL;

@Route(value = PAGE_CAROUSEL, layout = MainView.class)
@PageTitle(Constants.TITLE_CAROUSEL)
@Secured(Role.ADMIN)
public class CarouselView extends VerticalLayout implements HasLogger {

    private CarouselService carouselService = null;
    private Carousel c = new Carousel();

    public CarouselView(CarouselService carouselService) {
        this.carouselService = carouselService;

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

        Upload dropEnabledUpload = new Upload(buffer);
        dropEnabledUpload.setDropAllowed(true);

        Label dropEnabledLabel = new Label("Nueva imágen");
        dropEnabledLabel.getStyle().set("font-weight", "600");
        dropEnabledUpload.setId("upload-drop-enabled");
        dropEnabledLabel.setFor(dropEnabledUpload.getId().get());
        dropEnabledUpload.setAutoUpload(true);
        dropEnabledUpload.setAcceptedFileTypes("image/jpeg", ".jpg");

        UploadI18N i18N = new UploadI18N();
        i18N.setAddFiles(new UploadI18N.AddFiles().setOne("Subir imágenes...")
                .setMany("Subir imágenes..."));
        i18N.setDropFiles(new UploadI18N.DropFiles().setOne("Arrastrar imágenes aquí")
                .setMany("Arrastrar imágenes aquí"));
        i18N.setCancel("Cancelar");
        i18N.setError(new UploadI18N.Error().setTooManyFiles("Demasiado archivos.")
                .setFileIsTooBig("El archivo es demasiado grande.")
                .setIncorrectFileType("El tipo de archivo es incorrecto."));
        i18N.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status()
                        .setConnecting("Conectando...").setStalled("Detenido")
                        .setProcessing("Procesando archivo...").setHeld("Archivo"))
                .setRemainingTime(new UploadI18N.Uploading.RemainingTime()
                        .setPrefix("Tiempo restante: ")
                        .setUnknown("Tiempo restante no disponible"))
                .setError(new UploadI18N.Uploading.Error()
                        .setServerUnavailable("Servidor no responde")
                        .setUnexpectedServerError("Error inesperado")
                        .setForbidden("Denegado")));
        i18N.setUnits(new UploadI18N.Units().setSize(Arrays.asList("B", "kB", "MB", "GB", "TB",
                "PB", "EB", "ZB", "YB")));
        dropEnabledUpload.setI18n(i18N);

        dropEnabledUpload.addSucceededListener(listener -> {
            try {
                byte[] bytes = IOUtils.toByteArray(buffer.getInputStream(listener.getFileName()));
                this.uploadNewImage(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Paragraph hint = new Paragraph("Formato acpetado: JPG (.jpg)");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)");

        Div Section = new Div(dropEnabledLabel, hint, dropEnabledUpload);

        setAlignItems(Alignment.CENTER);

        /*
        grid.addColumn(
                TemplateRenderer
                        .<CarouselImages>of(
                                "<div><img style='height: 300px; width: 300px;' src='[[item.image]]' alt='[[item.id]]'/></div>"
                        )
                        .withProperty("image", item -> getImageAsBase64(item.getImage()))
                        .withProperty("id", item -> item.getId())
        ).setHeader("Imágen");
        grid.setItems(carouselService.findAll());
         */

        List<CarouselImages> imgList = carouselService.findAll();
        List<Slide> slides = new ArrayList<>();
        for (int i = 0; i < imgList.size(); i++) {
            Slide slideImg = new Slide(createSlideContent(imgList.get(i)));
            slides.add(slideImg);
        }

        c.setSlides(slides.toArray(new Slide[0]));

        c.withAutoProgress();
        c.withSlideDuration(2);
        c.withStartPosition(1);
        c.withoutSwipe();
        c.setHeightFull();
        c.setWidthFull();

        Button refreshButton = new Button("Eliminar imágenes");
        refreshButton.addClickListener(event -> {
            carouselService.delete();
            UI.getCurrent().getPage().reload();
        });

        this.add(new H2("Imágenes en Carrusel"), Section, refreshButton, c);
    }

    private String getImageAsBase64(byte[] string) {
        return "data:" + "image/jpeg" + ";base64," + Base64.encodeBase64String(string);
    }

    public void uploadNewImage(byte[] newImage) {
        CarouselImages newImg = new CarouselImages();
        newImg.setImage(newImage);
        carouselService.addItem(newImg);

        UI.getCurrent().getPage().reload();
    }

    private Component createSlideContent(CarouselImages img) {
        VerticalLayout d = new VerticalLayout();
        d.setAlignItems(Alignment.CENTER);
        d.setSizeFull();

        String url = "data:"+"image/jpeg"+";base64," + Base64.encodeBase64String(img.getImage());
        d.add(new Image(url, ""));
        return d;
    }
}
