package br.com.api.estudamais.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.estudamais.model.Follow;
import br.com.api.estudamais.model.User;
import br.com.api.estudamais.repository.FollowRepository;
import br.com.api.estudamais.service.UserService;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService usuarioService;

    @GetMapping
    public ResponseEntity<List<User>> obterTodosUsuarios() {
        List<User> usuarios = usuarioService.obterTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> obterUsuarioPorId(@PathVariable Long id) {
        Optional<User> usuario = usuarioService.obterUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> criarUsuario(@RequestBody User usuario) {
        User novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> atualizarUsuario(@PathVariable Long id, @RequestBody User novoUsuario) {
        User usuarioAtualizado = usuarioService.atualizarUsuario(id, novoUsuario);
        return usuarioAtualizado != null ? ResponseEntity.ok(usuarioAtualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/follow/{idSeguidor}/{idSeguindo}")
    public ResponseEntity<String> seguir(@PathVariable Long idSeguidor, @PathVariable Long idSeguindo) {
        usuarioService.seguirUsuario(idSeguidor, idSeguindo);
        return ResponseEntity.ok("Usuário seguido com sucesso!");
    }

    //method tunneling - para requisições via cliente
    @PostMapping("/unfollow/{idSeguidor}/{idSeguindo}")
    public ResponseEntity<String> deixarDeSeguir(@PathVariable Long idSeguidor, @PathVariable Long idSeguindo) {
        usuarioService.deixarDeSeguirUsuario(idSeguidor, idSeguindo);
        return ResponseEntity.ok("Usuário deixou de seguir com sucesso!");
    }

    @Autowired
    private FollowRepository followRepository;

    @GetMapping("/{userId}/seguidos")
    public ResponseEntity<List<User>> obterSeguidosPorUsuario(@PathVariable Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);

        List<User> usersFollowed = follows.stream()
                .map(Follow::getFollowing) // Supondo que o campo no Follow referencie o usuário seguido
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersFollowed);
    }

}