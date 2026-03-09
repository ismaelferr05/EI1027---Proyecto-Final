package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioOviController {

    @Autowired
    private UsuarioOviDao usuarioOviDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioOviDao.getAll());
        return "usuario/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("usuario", new UsuarioOvi());
        return "usuario/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute UsuarioOvi usuario) {
        usuarioOviDao.add(usuario);
        return "redirect:/usuarios/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("usuario", usuarioOviDao.get(id));
        return "usuario/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute UsuarioOvi usuario) {
        usuarioOviDao.update(usuario);
        return "redirect:/usuarios/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        usuarioOviDao.delete(id);
        return "redirect:/usuarios/list";
    }
}
