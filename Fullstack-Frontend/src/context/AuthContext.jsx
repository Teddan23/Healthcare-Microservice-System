import React, { createContext, useState, useContext } from "react";
import axios from "axios";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [users, setUsers] = useState([]);

    const login = async (personnummer, password) => {
        try {
            const response = await axios.post(`https://fullstack-userservice.app.cloud.cbh.kth.se/user/login`, {
                personnummer,
                password,
            });
            setUser({
                personnummer: response.data.personnummer,
                role: response.data.role,
                firstName: response.data.firstName,
                lastName: response.data.lastName,
            });

            sessionStorage.setItem("authToken", response.data.token);
            console.log(`Login successful. Token saved in sessionStorage. token is: ${response.data.token}`);
        } catch (error) {
            console.error("Login failed:", error);
            alert("Invalid personnummer or password");
        }
    };

    const register = (userDTO) => {
        const newUser = {
            personnummer: userDTO.personnummer,
            firstName: userDTO.firstName,
            lastName: userDTO.lastName,
            gender: userDTO.gender,
            birthDate: userDTO.birthDate,
            role: userDTO.role,
            username: userDTO.personnummer,
            password: "defaultpassword",
        };

        const userExists = users.some((u) => u.personnummer === newUser.personnummer);
        if (userExists) {
            alert("User with this personnummer already exists!");
            return;
        }

        setUsers([...users, newUser]);
        alert("Registration successful!");
    };

    const logout = () => {
        setUser(null);
        sessionStorage.removeItem("authToken");
        console.log("User logged out.");
    }

    return (
        <AuthContext.Provider value={{ user, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};
