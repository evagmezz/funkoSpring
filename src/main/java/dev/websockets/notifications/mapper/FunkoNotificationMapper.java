package dev.websockets.notifications.mapper;

import dev.rest.funkos.models.Funko;
import dev.websockets.notifications.dto.FunkoNotificationDto;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationDto toFunkoNotificationDto(Funko funko) {
        return new FunkoNotificationDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getRutaImagen(),
                funko.getFechaCreacion().toString(),
                funko.getFechaActualizacion().toString(),
                funko.getCategoria()
        );
    }
}
