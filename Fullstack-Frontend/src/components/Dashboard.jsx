import React, { useContext } from "react";
import { AuthContext } from "../context/AuthContext.jsx";
import PatientDashboard from "../pages/PatientDashboard.jsx";
import AdminDashboard from "../pages/AdminDashboard.jsx";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    if (!user) {
        return <div>Please login to continue</div>;
    }

    return (
        <div>
            <h2>Welcome, {user.name}!</h2>
            <button onClick={handleLogout}>Logout</button>
            {user.role === "patient" && <PatientDashboard />}
            {(user.role === "doctor" || user.role === "staff") && <AdminDashboard />}
        </div>
    );
};

export default Dashboard;
