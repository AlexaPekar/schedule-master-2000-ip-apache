package com.codecool.lms.servlet;

import com.codecool.lms.model.User;
import com.codecool.lms.service.UserService;
import com.codecool.lms.service.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/users")
public class UsersServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = UserServiceImpl.getUserService().getUsers();

        req.setAttribute("users", users);
        req.getRequestDispatcher("users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            UserServiceImpl.getUserService().getCurrentUser().setName(req.getParameter("newName"));
            UserServiceImpl.getUserService().getCurrentUser().setPassword(req.getParameter("newPassword"));
            req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}
