package dev.web.funkos.controllers;

import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.services.CategoriaService;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.services.FunkoService;
import dev.web.funkos.store.UserStore;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping(path = {"/funkos", "", "/"})
@Slf4j
public class FunkosWebController {
    private final FunkoService funkoService;
    private final CategoriaService categoriaService;
    private final UserStore userSession;

    @Autowired
    public FunkosWebController(FunkoService funkoService, CategoriaService categoriaService, UserStore userSession) {
        this.funkoService = funkoService;
        this.categoriaService = categoriaService;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("Si está logueado volvemos al index");
            return "redirect:/funkos";
        }
        return "login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return "redirect:/funkos";
        } else {
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        session.invalidate();
        return "redirect:/funkos";
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session,
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction,
                        Locale locale
    ) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        log.info("Index GET con parámetros search: " + search + ", page: " + page + ", size: " + size);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var funkosPage = funkoService.findAll(search, Optional.empty(), Optional.empty(), pageable);

        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisitas = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var localizedLastLoginDate = getLocalizedDate(lastLogin, locale);
        model.addAttribute("funkosPage", funkosPage);
        model.addAttribute("search", search.orElse(""));
        model.addAttribute("numVisitas", numVisitas);
        model.addAttribute("lastLoginDate", localizedLastLoginDate);
        return "index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, HttpSession session, Model model) {
        log.info("Details GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        FunkoResponseDto funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        log.info("Create GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        var categorias = categoriaService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getName);
        var funko = new FunkoCreateDto(
                "Funko",
                20.0,
                1,
                "imagen1",
                Categoria.builder().id(1L).build().toString()
        );
        model.addAttribute("funko", funko);
        model.addAttribute("categorias", categorias);
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("funko") FunkoCreateDto funkoDto, BindingResult result, Model model) {
        log.info("Create POST");
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getName);
            model.addAttribute("categorias", categorias);
            return "create";
        }
        funkoService.save(funkoDto);
        return "redirect:/funkos";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        var categorias = categoriaService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getName);
        FunkoResponseDto funko = funkoService.findById(id);
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(
                funko.nombre(),
                funko.precio(),
                funko.cantidad(),
                funko.rutaImagen(),
                funko.categoria().getName()
        );
        model.addAttribute("funko", funkoUpdateDto);
        model.addAttribute("categorias", categorias);
        return "update";
    }

    @PostMapping("/update/{id}")
    public String updateFunko(@PathVariable("id") Long id, @Valid @ModelAttribute("funko") FunkoUpdateDto funkoUpdateDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getName);
            model.addAttribute("categorias", categorias);
            return "update";
        }
        log.info("Update POST");
        System.out.println(id);
        System.out.println(funkoUpdateDto);
        var res = funkoService.update(funkoUpdateDto, id);
        System.out.println(res);
        return "redirect:/funkos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        funkoService.deleteById(id);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long funkoId, Model model, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        FunkoResponseDto funko = funkoService.findById(funkoId);
        model.addAttribute("funko", funko);
        return "update-img";
    }

    @PostMapping("/update-image/{id}")
    public String updateFunkoImage(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        funkoService.updateImage(funkoId, imagen, true);
        return "redirect:/funkos";
    }

    private String getLocalizedDate(Date date, Locale locale) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        return localDateTime.format(formatter);
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        log.info("Comprobando si está logueado");
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }
}