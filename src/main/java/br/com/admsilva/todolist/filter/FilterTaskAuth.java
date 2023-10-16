package br.com.admsilva.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import br.com.admsilva.todolist.repository.IUserRepository;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")) {
            var authorization = request.getHeader("Authorization");
            if (authorization == null) {
                response.sendError(401, "Nao foi possivel autenticar.");
                return;
            }
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecoded);
            String[] credentials = authString.split(":");
            if (credentials.length < 2) {
                response.sendError(401, "Nao foi possivel autenticar.");
                return;
            }
            String username = credentials[0];
            String password = credentials[1];
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(403, "Usuario sem autorizacao.");
                return;
            }
            var isCorrectPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (isCorrectPassword.verified) {
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
                return;
            }
            response.sendError(403, "Usuario sem autorizacao.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
