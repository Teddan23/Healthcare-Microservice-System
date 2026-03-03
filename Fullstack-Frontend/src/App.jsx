import React, { useContext } from "react";
import { Routes, Route } from "react-router-dom";
import LoginForm from "./components/LoginForm.jsx";
import RegistrationForm from './components/RegistrationForm.jsx';
import PatientDashboard from "./pages/PatientDashboard.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import { AuthContext } from './context/AuthContext.jsx';

const App = () => {
    const { user } = useContext(AuthContext);

    return (
        <Routes>
            <Route path="/" element={<LoginForm />} />
            <Route path="/register" element={<RegistrationForm />} />

            {user && user.role === 'patient' && (
                <Route path="/patient-dashboard" element={<PatientDashboard />} />
            )}

            {(user && (user.role === 'doctor' || user.role === 'staff')) && (
                <Route path="/admin-dashboard" element={<AdminDashboard />}>
                </Route>
            )}

            <Route path="*" element={<LoginForm />} />
        </Routes>
    );
};

export default App;
